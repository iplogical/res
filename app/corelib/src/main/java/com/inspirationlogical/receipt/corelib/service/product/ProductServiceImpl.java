package com.inspirationlogical.receipt.corelib.service.product;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.RecipeView;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.repository.ProductCategoryRepository;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.inspirationlogical.receipt.corelib.model.enums.ProductType.STORABLE;
import static java.util.stream.Collectors.toList;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private RecipeServiceImpl recipeService;

    @Override
    public void addProduct(ProductCategoryView parentCategoryView, Product.ProductBuilder builder) {
        Product newProduct = builder.build();
        ProductCategory parentCategory = productCategoryRepository.getOne(parentCategoryView.getId());
        if(isProductNameUsed(newProduct))
            throw new IllegalProductStateException(Resources.CONFIG.getString("ProductNameAlreadyUsed") + newProduct.getLongName());
        ProductCategory pseudo = buildPseudoCategory(newProduct, parentCategory);
        bindProductToPseudo(newProduct, parentCategory, pseudo);
        addDefaultRecipe(newProduct);
        productRepository.save(newProduct);
    }

    private boolean isProductNameUsed(Product product) {
        return productRepository.findByLongName(product.getLongName()) != null;
    }

    private ProductCategory buildPseudoCategory(Product newProduct, ProductCategory parentCategory) {
        return ProductCategory.builder()
                .name(newProduct.getLongName() + "pseudo")
                .type(ProductCategoryType.PSEUDO)
                .status(ProductStatus.ACTIVE)
                .parent(parentCategory)
                .product(newProduct)
                .build();
    }

    private void bindProductToPseudo(Product newProduct, ProductCategory parentCategory, ProductCategory pseudo) {
        parentCategory.getChildren().add(pseudo);
        newProduct.setCategory(pseudo);
    }

    private void addDefaultRecipe(Product newProduct) {
        newProduct.setRecipes(new ArrayList<>(Collections.singletonList(buildRecipe(newProduct))));
    }

    private Recipe buildRecipe(Product newProduct) {
        return Recipe.builder()
                .component(newProduct)
                .quantityMultiplier(1)
                .owner(newProduct)
                .build();
    }

    @Override
    public void updateProduct(int productId, String parentCategoryName, Product.ProductBuilder builder) {
        Product originalProduct = productRepository.getOne(productId);
        Product updatedProduct = builder.build();
        if(isProductNameAlreadyUsed(originalProduct, updatedProduct))
            throw new IllegalProductStateException(Resources.CONFIG.getString("ProductNameAlreadyUsed") + updatedProduct.getLongName());
        setProductParameters(originalProduct, updatedProduct);
        if(isCategoryChanged(originalProduct, parentCategoryName)) {
            movePseudoToNewParent(originalProduct, parentCategoryName);
        }
        productRepository.save(originalProduct);
    }

    private boolean isProductNameAlreadyUsed(Product originalProduct, Product updatedProduct) {
        Product product = productRepository.findByLongName(updatedProduct.getLongName());
        return !(product == null || product.getId() == originalProduct.getId());
    }

    private void setProductParameters(Product originalProduct, Product updatedProduct) {
        originalProduct.setLongName(updatedProduct.getLongName());
        originalProduct.setShortName(updatedProduct.getShortName());
        originalProduct.setType(updatedProduct.getType());
        setAdapteeAndPseudoStatus(originalProduct, updatedProduct.getStatus());
        originalProduct.setRapidCode(updatedProduct.getRapidCode());
        originalProduct.setQuantityUnit(updatedProduct.getQuantityUnit());
        originalProduct.setStorageMultiplier(updatedProduct.getStorageMultiplier());
        originalProduct.setSalePrice(updatedProduct.getSalePrice());
        originalProduct.setPurchasePrice(updatedProduct.getPurchasePrice());
        originalProduct.setMinimumStock(updatedProduct.getMinimumStock());
        originalProduct.setStockWindow(updatedProduct.getStockWindow());
        originalProduct.setOrderNumber(updatedProduct.getOrderNumber());
    }

    private void setAdapteeAndPseudoStatus(Product originalProduct, ProductStatus status) {
        originalProduct.setStatus(status);
        originalProduct.getCategory().setStatus(status);
    }

    private boolean isCategoryChanged(Product originalProduct, String parentCategoryName) {
        return !originalProduct.getCategory().getParent().getName().equals(parentCategoryName);
    }

    private void movePseudoToNewParent(Product originalProduct, String parentCategoryName) {
        ProductCategory originalCategory = productCategoryRepository.findByName(originalProduct.getCategory().getParent().getName());
        ProductCategory newCategory = productCategoryRepository.findByName(parentCategoryName);
        ProductCategory pseudoCategory = productCategoryRepository.findByName((originalProduct.getCategory().getName()));
        originalCategory.getChildren().remove(pseudoCategory);
        pseudoCategory.setParent(newCategory);
        newCategory.getChildren().add(originalProduct.getCategory());
    }

    @Override
    public void deleteProduct(String longName) {
        Product toDelete = productRepository.findByLongName(longName);
        setAdapteeAndPseudoStatus(toDelete, ProductStatus.DELETED);
        productRepository.save(toDelete);
    }

    @Override
    public List<RecipeView> getRecipeComponents(ProductView productView) {
        return recipeService.getRecipeComponents(productView);
    }

    @Override
    public void updateRecipe(ProductView owner, List<RecipeParams> recipeParamsList) {
        recipeService.updateRecipes(owner, recipeParamsList);
        recipeService.addRecipes(owner, recipeParamsList);
        recipeService.deleteRecipes(owner, recipeParamsList);
    }

    @Override
    public List<Product> getStorableProducts() {
        List<Product> activeProducts = productRepository.findAllByStatus(ProductStatus.ACTIVE);

        List<Product> productsAsRecipe = activeProducts.stream()
                .flatMap(product -> product.getRecipes().stream())
                .map(Recipe::getComponent)
                .filter(distinctByKey(Product::getLongName))
                .collect(toList());
        List<Product> storableProducts = activeProducts.stream()
                .filter(product -> product.getType().equals(STORABLE))
                .collect(toList());
        productsAsRecipe.addAll(storableProducts);
        return productsAsRecipe.stream()
                .filter(distinctByKey(Product::getLongName))
                .collect(toList());
    }

    private  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public List<ProductView> getProductsByCategory(ProductCategoryView productCategoryView, boolean showDeleted) {
        ProductCategory productCategory = productCategoryRepository.findById(productCategoryView.getId());
        List<ProductCategory> pseudoCategories = new ArrayList<>();
        
        List<ProductCategory> childrenCategories = productCategory.getChildren();
        return new ArrayList<>();
    }
}
