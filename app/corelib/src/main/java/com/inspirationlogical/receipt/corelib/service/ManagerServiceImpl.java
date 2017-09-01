package com.inspirationlogical.receipt.corelib.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.adapter.*;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Singleton
public class ManagerServiceImpl extends AbstractService implements ManagerService {

    @Inject
    ManagerServiceImpl(EntityViews entityViews) {
        super(entityViews);
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
        ProductCategoryAdapter.getProductCategoryByName(params.getOriginalName()).updateProductCategory(params);
    }

    @Override
    public void deleteProductCategory(String name) {
        ProductCategoryAdapter.getProductCategoryByName(name).deleteProductCategory();
    }

    @Override
    public void updateStock(List<StockParams> params, ReceiptType receiptType, StockListener.StockUpdateListener listener) {
        TableAdapter.getTablesByType(TableType.getTableType(receiptType)).get(0)
                .updateStock(params, receiptType, listener);
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
