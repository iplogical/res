package com.inspirationlogical.receipt.corelib.service;

import java.util.List;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.adapter.*;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import static java.util.stream.Collectors.toList;

public class CommonServiceImpl extends AbstractService implements CommonService {

    @Inject
    public CommonServiceImpl(EntityManager manager) {
        super(manager);
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
    public ProductView addProduct(ProductCategoryView parent, Product.ProductBuilder builder) {
        return new ProductViewImpl(getProductCategoryAdapter(parent).addProduct(builder));
    }

    @Override
    public void deleteProduct(String longName) {
        new ProductAdapter(ProductAdapter.getProductByName(longName).get(0)).delete();
    }

    @Override
    public ProductCategoryView addProductCategory(ProductCategoryParams params) {
        return new ProductCategoryViewImpl(getProductCategoryAdapter(params.getParent())
                .addChildCategory(params.getName(), params.getType()));
    }

    @Override
    public ProductCategoryView updateProductCategory(ProductCategoryParams params) {
        return new ProductCategoryViewImpl(getProductCategoryAdapter(params.getParent())
                .updateChildCategory(params.getName(), params.getOriginalName(), params.getType()));
    }

    @Override
    public void deleteProductCategory(String name) {
        new ProductCategoryAdapter(ProductCategoryAdapter.getProductCategoryByName(name).get(0)).delete();
    }

    @Override
    public void updateStock(List<StockParams> params, ReceiptType receiptType) {
        TableAdapter.getTablesByType(TableType.getTableType(receiptType)).stream()
                .map(TableAdapter::new)
                .collect(toList())
                .get(0).updateStock(params, receiptType);
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
        return new ProductCategoryViewImpl(ProductCategoryAdapter.getRootCategory());
    }

    @Override
    public List<ProductCategoryView> getAllCategories() {
        List<ProductCategoryView> categoryViews =
                createViewsFromAdapters(ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.ROOT),ProductCategoryViewImpl::new);
        categoryViews.addAll(createViewsFromAdapters(ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.AGGREGATE),ProductCategoryViewImpl::new));
        categoryViews.addAll(createViewsFromAdapters(ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.LEAF),ProductCategoryViewImpl::new));
        return categoryViews;
    }

    @Override
    public List<ProductCategoryView> getAggregateCategories() {
        return ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.AGGREGATE).stream().map(ProductCategoryViewImpl::new)
                .collect(toList());
    }

    @Override
    public List<ProductCategoryView> getLeafCategories() {
        return ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.LEAF).stream().map(ProductCategoryViewImpl::new)
                .collect(toList());
    }

    @Override
    public List<ProductView> getSellableProducts() {
        return createViewsFromAdapters(ProductCategoryAdapter.getRootCategory().getAllSellableProducts(), ProductViewImpl::new);
    }

    @Override
    public List<ProductView> getStorableProducts() {
        return createViewsFromAdapters(ProductCategoryAdapter.getRootCategory().getAllStorableProducts(), ProductViewImpl::new);
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
}
