package com.inspirationlogical.receipt.corelib.service.product;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductStateException;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.entity.VAT;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.RecipeView;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.repository.ProductCategoryRepository;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.repository.VATRepository;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private VATRepository vatRepository;

    @Override
    public void addProduct(ProductView productView, ProductCategoryView parent) {
        Product newProduct = buildProduct(productView);
        ProductCategory parentCategory = productCategoryRepository.getOne(parent.getId());
        if(isProductNameUsed(newProduct))
            throw new IllegalProductStateException(Resources.CONFIG.getString("ProductNameAlreadyUsed") + newProduct.getLongName());
        ProductCategory pseudo = buildPseudoCategory(newProduct, parentCategory);
        bindProductToPseudo(newProduct, parentCategory, pseudo);
        addDefaultRecipe(newProduct);
        productRepository.save(newProduct);
    }

    private Product buildProduct(ProductView productView) {
        VAT vat = vatRepository.getVatByName(productView.getVat());
        return Product.builder()
                .longName(productView.getLongName())
                .shortName(productView.getShortName())
                .type(productView.getType())
                .status(productView.getStatus())
                .VATLocal(vat)
                .VATTakeAway(vat)
                .rapidCode(productView.getRapidCode())
                .quantityUnit(productView.getQuantityUnit())
                .storageMultiplier(productView.getStorageMultiplier())
                .purchasePrice(productView.getPurchasePrice())
                .salePrice(productView.getSalePrice())
                .minimumStock(productView.getMinimumStock())
                .stockWindow(productView.getStockWindow())
                .orderNumber(productView.getOrderNumber())
                .build();
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
    public void updateProduct(ProductView productView, ProductCategoryView parent) {
        Product originalProduct = productRepository.getOne(productView.getId());
        Product updatedProduct = buildProduct(productView);
        if(isProductNameAlreadyUsed(originalProduct, updatedProduct))
            throw new IllegalProductStateException(Resources.CONFIG.getString("ProductNameAlreadyUsed") + updatedProduct.getLongName());
        setProductParameters(originalProduct, updatedProduct);
        String parentCategoryName = parent.getCategoryName();
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
        originalProduct.setVATLocal(updatedProduct.getVATLocal());
        originalProduct.setVATTakeAway(updatedProduct.getVATTakeAway());
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
        ProductCategory selectedProductCategory = productCategoryRepository.findById(productCategoryView.getId());
        if(selectedProductCategory.getType() == ProductCategoryType.LEAF) {
            return buildProductViewList(selectedProductCategory.getChildren(), showDeleted);
        }
        List<ProductCategory> pseudoCategories = extractPseudoCategories(selectedProductCategory);
        return buildProductViewList(pseudoCategories, showDeleted);
    }

    private List<ProductView> buildProductViewList(List<ProductCategory> productCategoryList, boolean showDeleted) {
        return productCategoryList.stream()
                .map(ProductCategory::getProduct)
                .filter(product -> showDeleted || product.getStatus() == ProductStatus.ACTIVE)
                .map(ProductView::new)
                .sorted(Comparator.comparing(ProductView::getOrderNumber))
                .collect(toList());
    }

    private List<ProductCategory> extractPseudoCategories(ProductCategory selectedProductCategory) {
        List<ProductCategory> leafCategories = extractLeafCategories(selectedProductCategory);
        return leafCategories.stream()
                .map(ProductCategory::getChildren)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private List<ProductCategory> extractLeafCategories(ProductCategory selectedProductCategory) {
        List<ProductCategory> aggregateCategories = getProductCategoriesByType(selectedProductCategory.getChildren(), ProductCategoryType.AGGREGATE);
        List<ProductCategory> leafCategories = getProductCategoriesByType(selectedProductCategory.getChildren(), ProductCategoryType.LEAF);
        while(!aggregateCategories.isEmpty()) {
            List<ProductCategory> childrenCategories = aggregateCategories.stream()
                    .map(ProductCategory::getChildren)
                    .flatMap(Collection::stream)
                    .collect(toList());
            aggregateCategories = getProductCategoriesByType(childrenCategories, ProductCategoryType.AGGREGATE);
            leafCategories.addAll(getProductCategoriesByType(childrenCategories, ProductCategoryType.LEAF));
        }
        return leafCategories;
    }

    private List<ProductCategory> getProductCategoriesByType(List<ProductCategory> productCategoryList, ProductCategoryType type) {
        return productCategoryList.stream()
                .filter(productCategory -> productCategory.getType() == type)
                .collect(toList());
    }
}
