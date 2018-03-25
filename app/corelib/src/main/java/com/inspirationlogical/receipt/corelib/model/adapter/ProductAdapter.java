package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

import java.util.List;
import java.util.Map;

import static com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter.getProductCategoryByName;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ProductAdapter extends AbstractAdapter<Product> {

    public static ProductAdapter getProductByName(String name) {
        List<Product> products = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_NAME,
                query -> query.setParameter("longName", name));
        if(products.size() == 1) {
            return new ProductAdapter(products.get(0));
        }
        return null;
    }

    public static ProductAdapter getProductById(long productId) {
        List<Product> productList = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_ID,
                query -> query.setParameter("id", productId));
        return new ProductAdapter(productList.get(0));
    }

    public static List<ProductAdapter> getActiveProducts() {
        List<Product> products = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_STATUS,
                query -> {query.setParameter("status", ProductStatus.ACTIVE);
                    return query;});
        return products.stream().map(ProductAdapter::new).collect(toList());
    }

//    public static List<ProductAdapter> getStorableProducts() {
//        return getActiveProducts().stream()
//                .flatMap(productAdapter -> productAdapter.getAdaptee().getRecipes().stream())
//                .map(recipe -> new ProductAdapter(recipe.getComponent()))
//                .filter(distinctByKey(productAdapter -> productAdapter.getAdaptee().getLongName()))
//                .collect(toList());
//    }
//
//    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
//        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
//        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
//    }

//    public static ProductAdapter getAdHocProduct() {
//                List<Product> adHocProductList = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_TYPE,
//                        query -> {
//                    query.setParameter("type", ProductType.AD_HOC_PRODUCT);
//                    return query;
//                });
//        if (adHocProductList.isEmpty()) {
//            throw new AdHocProductNotFoundException();
//        }
//        return new ProductAdapter(adHocProductList.get(0));
//    }
//
//    public static ProductAdapter getGameFeeProduct() {
//        List<Product> gameFeeProductList = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_TYPE,
//                query -> query.setParameter("type", ProductType.GAME_FEE_PRODUCT));
//        if(gameFeeProductList.isEmpty()) {
//            throw new GameFeeProductNotFoundException();
//        }
//        return new ProductAdapter(gameFeeProductList.get(0));
//    }
    
    public ProductAdapter(Product adaptee) {
        super(adaptee);
    }


    public ProductCategoryAdapter getCategoryAdapter() {
        return new ProductCategoryAdapter(adaptee.getCategory());
    }


    public void deleteProduct() {
        GuardedTransaction.run(() -> {
            setAdapteeAndPseudoStatus(ProductStatus.DELETED);
        });
    }

    public ProductAdapter updateProduct(String parentCategoryName, Product.ProductBuilder builder) {
        GuardedTransaction.run(() -> {
            Product productToBuild = builder.build();
            if(isProductNameAlreadyUsed(productToBuild))
                throw new IllegalProductStateException(Resources.CONFIG.getString("ProductNameAlreadyUsed") + productToBuild.getLongName());
            setProductParameters(productToBuild);
        });
        if(isCategoryChanged(parentCategoryName)) {
            movePseudoToNewParent(parentCategoryName);
        }
        return this;
    }

    private boolean isProductNameAlreadyUsed(Product productToBuild) {
        ProductAdapter product = getProductByName(productToBuild.getLongName());
        return !(product == null || product.getAdaptee().getId().equals(adaptee.getId()));
    }

    private void setProductParameters(Product productToBuild) {
        adaptee.setLongName(productToBuild.getLongName());
        adaptee.setShortName(productToBuild.getShortName());
        adaptee.setType(productToBuild.getType());
        setAdapteeAndPseudoStatus(productToBuild.getStatus());
        adaptee.setRapidCode(productToBuild.getRapidCode());
        adaptee.setQuantityUnit(productToBuild.getQuantityUnit());
        adaptee.setStorageMultiplier(productToBuild.getStorageMultiplier());
        adaptee.setSalePrice(productToBuild.getSalePrice());
        adaptee.setPurchasePrice(productToBuild.getPurchasePrice());
        adaptee.setMinimumStock(productToBuild.getMinimumStock());
        adaptee.setStockWindow(productToBuild.getStockWindow());
        adaptee.setOrderNumber(productToBuild.getOrderNumber());
    }

    private void setAdapteeAndPseudoStatus(ProductStatus status) {
        adaptee.setStatus(status);
        adaptee.getCategory().setStatus(status);
    }

    private boolean isCategoryChanged(String parentCategoryName) {
        return !adaptee.getCategory().getParent().getName().equals(parentCategoryName);
    }

    private void movePseudoToNewParent(String parentCategoryName) {
        GuardedTransaction.run(() -> {
            ProductCategory originalCategory = getProductCategoryByName(adaptee.getCategory().getParent().getName()).getAdaptee();
            ProductCategory newCategory = getProductCategoryByName(parentCategoryName).getAdaptee();
            ProductCategory pseudoCategory = getProductCategoryByName(adaptee.getCategory().getName()).getAdaptee();
            originalCategory.getChildren().remove(pseudoCategory);
            pseudoCategory.setParent(newCategory);
        });
        GuardedTransaction.run(() -> {
            ProductCategory newCategory = getProductCategoryByName(parentCategoryName).getAdaptee();
            newCategory.getChildren().add(adaptee.getCategory());
        });
    }
    public void updateRecipes(List<RecipeParams> recipeParamsList) {
        List<RecipeAdapter> components = RecipeAdapter.getRecipesOfProduct(this);
        Map<String, Double> recipeParamsMap = getRecipesMap(recipeParamsList);
        List<RecipeAdapter> recipesToDelete = getRecipesToDelete(components, recipeParamsMap);
        components.removeAll(recipesToDelete);

        updateComponents(components, recipeParamsMap);
    }

    public void addRecipes(List<RecipeParams> recipeParamsList) {
        List<RecipeAdapter> components = RecipeAdapter.getRecipesOfProduct(this);
        Map<String, Double> componentsMap = getComponentsMap(components);
        Map<String, Double> recipesToAdd = getRecipesToAdd(recipeParamsList, componentsMap);

        addComponents(recipesToAdd);
    }

    public void deleteRecipes(List<RecipeParams> recipeParamsList) {
        List<RecipeAdapter> components = RecipeAdapter.getRecipesOfProduct(this);
        Map<String, Double> recipeParamsMap = getRecipesMap(recipeParamsList);
        List<RecipeAdapter> recipesToDelete = getRecipesToDelete(components, recipeParamsMap);

        deleteComponents(recipesToDelete);
    }

    private Map<String, Double> getComponentsMap(List<RecipeAdapter> components) {
        return components.stream().collect(toMap(component -> component.getAdaptee().getComponent().getLongName(), component -> component.getAdaptee().getQuantityMultiplier()));
    }

    private Map<String, Double> getRecipesMap(List<RecipeParams> recipeParamsList) {
        return recipeParamsList.stream().collect(toMap(RecipeParams::getComponentName, RecipeParams::getQuantity));
    }

    private Map<String, Double> getRecipesToAdd(List<RecipeParams> recipeParamsList, Map<String, Double> componentsMap) {
        return recipeParamsList.stream()
                .filter(recipeParams -> !componentsMap.containsKey(recipeParams.getComponentName()))
                .collect(toMap(RecipeParams::getComponentName, RecipeParams::getQuantity));
    }

    private List<RecipeAdapter> getRecipesToDelete(List<RecipeAdapter> components, Map<String, Double> recipeParamsMap) {
        return components.stream()
                .filter(component -> !recipeParamsMap.containsKey(component.getAdaptee().getComponent().getLongName()))
                .collect(toList());
    }

    private void updateComponents(List<RecipeAdapter> components, Map<String, Double> recipeParamsMap) {
        components.forEach(recipeAdapter -> {
            String componentName = recipeAdapter.getAdaptee().getComponent().getLongName();
            recipeAdapter.getAdaptee().setQuantityMultiplier(recipeParamsMap.get(componentName));
            GuardedTransaction.persist(recipeAdapter.getAdaptee());
        });
    }

    private void deleteComponents(List<RecipeAdapter> recipesToDelete) {
        recipesToDelete.forEach(toDelete -> {
            GuardedTransaction.delete(toDelete.getAdaptee(), () -> adaptee.getRecipes().remove(toDelete.getAdaptee()));
        });
    }

    private void addComponents(Map<String, Double> recipesToAdd) {
        recipesToAdd.forEach((key, value) -> {
            Product component = getProductByName(key).getAdaptee();
            addNewComponent(value, component);
        });
    }

    private void addNewComponent(Double value, Product component) {
        Recipe newComponent = Recipe.builder().component(component).quantityMultiplier(value).owner(adaptee).build();
        adaptee.getRecipes().add(newComponent);
        GuardedTransaction.persist(newComponent);
    }
}