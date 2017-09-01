package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.util.List;

public interface ManagerService {

    PriceModifier.PriceModifierBuilder priceModifierBuilder();

    void addProduct(ProductCategoryView parent, Product.ProductBuilder builder);

    void updateProduct(Long productId, ProductCategoryView parent, Product.ProductBuilder builder);

    void deleteProduct(String longName);

    void addProductCategory(ProductCategoryParams params);

    void updateProductCategory(ProductCategoryParams params);

    void deleteProductCategory(String name);

    void updateStock(List<StockParams> params, ReceiptType receiptType, StockListener.StockUpdateListener listener);

    void addPriceModifier(PriceModifierParams params);

    void updateRecipe(ProductView owner, List<RecipeParams> recipeParamsList);

    List<StockView> getStockItems();

    List<PriceModifierView> getPriceModifiers();

    List<RecipeView> getRecipeComponents(ProductView product);

    List<ReceiptView> getReceipts();
}
