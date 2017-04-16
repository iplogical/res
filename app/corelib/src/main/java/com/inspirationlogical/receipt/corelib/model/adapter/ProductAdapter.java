package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.List;
import java.util.Map;

import com.inspirationlogical.receipt.corelib.exception.AdHocProductNotFoundException;
import com.inspirationlogical.receipt.corelib.exception.GameFeeProductNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class ProductAdapter extends AbstractAdapter<Product> {

    static ProductAdapter getAdHocProduct() {
                List<Product> adHocProductList = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_TYPE,
                        query -> {
                    query.setParameter("type", ProductType.AD_HOC_PRODUCT);
                    return query;
                });
        if (adHocProductList.isEmpty()) {
            throw new AdHocProductNotFoundException();
        }
        return new ProductAdapter(adHocProductList.get(0));
    }

    static ProductAdapter getGameFeeProduct() {
        List<Product> gameFeeProductList = GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_TYPE,
                query -> query.setParameter("type", ProductType.GAME_FEE_PRODUCT));
        if(gameFeeProductList.isEmpty()) {
            throw new GameFeeProductNotFoundException();
        }
        return new ProductAdapter(gameFeeProductList.get(0));
    }

    static List<Product> getProductByName(String name) {
        return GuardedTransaction.runNamedQuery(Product.GET_PRODUCT_BY_NAME,
                query -> {
                    query.setParameter("longName", name);
                    return query;});
    }

    public ProductAdapter(Product adaptee) {
        super(adaptee);
    }


    ProductCategoryAdapter getCategoryAdapter() {
        return new ProductCategoryAdapter(adaptee.getCategory());
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
        GuardedTransaction.run(() -> components.stream().forEach(recipeAdapter -> recipeAdapter.getAdaptee().setQuantityMultiplier(recipeParamsMap.get(recipeAdapter.getAdaptee().getComponent().getLongName()))));
    }

    private void deleteComponents(List<RecipeAdapter> recipesToDelete) {
        recipesToDelete.stream().forEach(toDelete -> {
            GuardedTransaction.delete(toDelete.getAdaptee(), () -> adaptee.getRecipes().remove(toDelete.getAdaptee()));
        });
    }

    private void addComponents(Map<String, Double> recipesToAdd) {
        recipesToAdd.forEach((key, value) -> {
            Product component = getProductByName(key).get(0);
            GuardedTransaction.run(() -> addNewComponent(value, component));
        });
    }

    private void addNewComponent(Double value, Product component) {
        Recipe newComponent = Recipe.builder().component(component).quantityMultiplier(value).owner(adaptee).build();
        adaptee.getRecipes().add(newComponent);
    }
}
