package com.inspirationlogical.receipt.corelib.service;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Product.ProductBuilder;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier.PriceModifierBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

public interface CommonService {
    ProductBuilder productBuilder();

    PriceModifierBuilder priceModifierBuilder();

    void addProduct(ProductCategoryView parent, ProductBuilder builder);

    void updateProduct(Long productId, ProductCategoryView parent, ProductBuilder builder);

    void deleteProduct(String longName);

    void addProductCategory(ProductCategoryParams params);

    void updateProductCategory(ProductCategoryParams params);

    void deleteProductCategory(String name);

    void updateStock(List<StockParams> params, ReceiptType receiptType);

    void addPriceModifier(PriceModifierParams params);

    void updateRecipe(ProductView owner, List<RecipeParams> recipeParamsList);

    ProductCategoryView getRootProductCategory();

    List<ProductCategoryView> getAllCategories();

    List<ProductCategoryView> getAggregateCategories();

    List<ProductCategoryView> getLeafCategories();

    List<ProductCategoryView> getChildCategories(ProductCategoryView productCategoryView);

    void getChildCategoriesRecursively(ProductCategoryView current, List<ProductCategoryView> traversal);

    List<ProductCategoryView> getChildPseudoCategories(ProductCategoryView productCategoryView);

    List<ProductView> getActiveProducts();

    List<ProductView> getSellableProducts();

    List<ProductView> getSellableProducts(ProductCategoryView productCategoryView);

    List<ProductView> getStorableProducts();

    List<StockView> getStockItems();

    List<PriceModifierView> getPriceModifiers();

    List<RecipeView> getRecipeComponents(ProductView product);

    List<ReceiptView> getReceipts();
}
