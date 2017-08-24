package com.inspirationlogical.receipt.corelib.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.exception.RootCategoryNotFoundException;
import com.inspirationlogical.receipt.corelib.model.adapter.*;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.util.ArrayList;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter.distinctByKey;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.*;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductStatus.ACTIVE;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductType.*;
import static java.util.stream.Collectors.toList;

@Singleton
public class CommonServiceImpl extends AbstractService implements CommonService {

    @Inject
    public CommonServiceImpl(EntityViews entityViews) {
        super(entityViews);
    }

    @Override
    public Product.ProductBuilder productBuilder() {
        return Product.builder();
    }

    @Override
    public PriceModifier.PriceModifierBuilder priceModifierBuilder() {
        return PriceModifier.builder();
    }

    @Override
    public void addProduct(ProductCategoryView parent, Product.ProductBuilder builder) {
        getProductCategoryAdapter(parent).addProduct(builder);
        entityViews.initEntityViews();
    }

    @Override
    public void updateProduct(Long productId, ProductCategoryView parent, Product.ProductBuilder builder) {
        ProductAdapter.getProductById(productId).updateProduct(parent.getCategoryName(), builder);
    }

    @Override
    public void deleteProduct(String longName) {
        ProductAdapter.getProductByName(longName).deleteProduct();
    }

    @Override
    public void addProductCategory(ProductCategoryParams params) {
        getProductCategoryAdapter(params.getParent()).addChildCategory(params);
        entityViews.initEntityViews();
    }

    @Override
    public void updateProductCategory(ProductCategoryParams params) {
        ProductCategoryAdapter.updateProductCategory(params);
    }

    @Override
    public void deleteProductCategory(String name) {
        ProductCategoryAdapter.getProductCategoryByName(name).deleteProductCategory();
    }

    @Override
    public void updateStock(List<StockParams> params, ReceiptType receiptType) {
        TableAdapter.getTablesByType(TableType.getTableType(receiptType)).get(0)
                .updateStock(params, receiptType);
    }

    @Override
    public void addPriceModifier(PriceModifierParams params) {
        PriceModifierAdapter.addPriceModifier(params);
    }

    @Override
    public void updateRecipe(ProductView owner, List<RecipeParams> recipeParamsList) {
        ProductAdapter ownerProduct = getProductAdapter(owner);
        ownerProduct.updateRecipes(recipeParamsList);
        ownerProduct.addRecipes(recipeParamsList);
        ownerProduct.deleteRecipes(recipeParamsList);
    }

    @Override
    public ProductCategoryView getRootProductCategory() {
        return entityViews.getCategoryViews().stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(ROOT))
                .findFirst().orElseThrow(RootCategoryNotFoundException::new);
    }

    @Override
    public List<ProductCategoryView> getAllCategories() {
        return entityViews.getCategoryViews();
    }

    @Override
    public List<ProductCategoryView> getAggregateCategories() {
        return entityViews.getCategoryViews().stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(AGGREGATE))
                .collect(toList());
    }

    @Override
    public List<ProductCategoryView> getLeafCategories() {
        return entityViews.getCategoryViews().stream()
                .filter(productCategoryView -> productCategoryView.getType().equals(LEAF))
                .collect(toList());
    }

    @Override
    public List<ProductCategoryView> getChildCategories(ProductCategoryView productCategoryView) {
        return entityViews.getCategoryViews().stream()
                .filter(categoryView -> isNotRootCategory(categoryView) && isMyChild(productCategoryView, categoryView))
                .collect(toList());
    }

    private boolean isMyChild(ProductCategoryView productCategoryView, ProductCategoryView categoryView) {
        return categoryView.getParent().getCategoryName().equals(productCategoryView.getCategoryName());
    }

    private boolean isNotRootCategory(ProductCategoryView categoryView) {
        return !categoryView.getType().equals(ROOT);
    }

    @Override
    public void getChildCategoriesRecursively(ProductCategoryView current, List<ProductCategoryView> traversal) {
        traversal.add(current);
        entityViews.getCategoryViews().stream()
                .filter(categoryView -> isNotRootCategory(categoryView) && isMyChild(current, categoryView))
                .forEach(categoryView -> getChildCategoriesRecursively(categoryView, traversal));
    }

    @Override
    public List<ProductCategoryView> getChildPseudoCategories(ProductCategoryView productCategoryView) {
        List<ProductCategoryView> childCategories = new ArrayList<>();
        getChildCategoriesRecursively(productCategoryView, childCategories);
        return childCategories.stream()
                .filter(productCategory -> productCategory.getType().equals(PSEUDO))
                .collect(toList());
    }

    @Override
    public List<ProductView> getActiveProducts() {
        return entityViews.getProductViews().stream()
                .filter(productView -> productView.getStatus().equals(ACTIVE))
                .collect(toList());
    }

    @Override
    public List<ProductView> getSellableProducts() {
        return getActiveProducts().stream()
                .filter(productView -> !productView.getType().equals(AD_HOC_PRODUCT))
                .filter(productView -> !productView.getType().equals(GAME_FEE_PRODUCT))
                .filter(productView -> !productView.getType().equals(STORABLE))
                .collect(toList());
    }

    @Override
    public List<ProductView> getSellableProducts(ProductCategoryView productCategoryView) {
        return getChildPseudoCategories(productCategoryView).stream()
                .map(ProductCategoryView::getProduct)
                .filter(productView -> !productView.getType().equals(AD_HOC_PRODUCT))
                .filter(productView -> !productView.getType().equals(GAME_FEE_PRODUCT))
                .filter(productView -> !productView.getType().equals(STORABLE))
                .collect(toList());
    }

    @Override
    public List<ProductView> getStorableProducts() {
        List<ProductView> productsAsRecipe = getActiveProducts().stream()
                .flatMap(productView -> productView.getRecipes().stream())
                .map(RecipeView::getComponent)
                .filter(distinctByKey(ProductView::getLongName))
                .collect(toList());
        List<ProductView> storableProducts = getActiveProducts().stream()
                .filter(productView -> productView.getType().equals(STORABLE))
                .collect(toList());
        productsAsRecipe.addAll(storableProducts);
        return productsAsRecipe.stream()
                .filter(distinctByKey(ProductView::getLongName))
                .collect(toList());
    }

    @Override
    public List<StockView> getStockItems() {
        return createViewsFromAdapters(StockAdapter.getItems(), StockViewImpl::new);
    }

    @Override
    public List<PriceModifierView> getPriceModifiers() {
        return createViewsFromAdapters(PriceModifierAdapter.getPriceModifiers(), PriceModifierViewImpl::new);
    }

    @Override
    public List<RecipeView> getRecipeComponents(ProductView product) {
        return createViewsFromAdapters(RecipeAdapter.getRecipesOfProduct(getProductAdapter(product)), RecipeViewImpl::new);
    }

    @Override
    public List<ReceiptView> getReceipts() {
        return ReceiptAdapter.getReceipts()
                .stream()
                .map(ReceiptViewImpl::new)
                .collect(toList());
    }
}
