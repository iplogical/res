package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

import java.time.LocalDate;
import java.util.List;

public interface ManagerService {

    PriceModifier.PriceModifierBuilder priceModifierBuilder();

    void addProduct(ProductCategoryView parent, Product.ProductBuilder builder);

    void updateProduct(int productId, ProductCategoryView parent, Product.ProductBuilder builder);

    void deleteProduct(String longName);

    List<ProductView> getProductsByCategory(ProductCategoryView productCategoryView, boolean showDeleted);

    void addProductCategory(ProductCategoryParams params);

    void updateProductCategory(ProductCategoryParams params);

    void deleteProductCategory(String name);

    void updateStock(List<StockParams> params, ReceiptType receiptType);

    void addPriceModifier(PriceModifierParams params);

    void updatePriceModifier(PriceModifierParams params);

    void deletePriceModifier(PriceModifierParams params);

    void updateRecipe(ProductView owner, List<RecipeParams> recipeParamsList);

    List<StockView> getStockViewListByCategory(ProductCategoryView selectedCategory);

    List<PriceModifierView> getPriceModifiers();

    List<RecipeView> getRecipeComponents(ProductView product);

    List<ReceiptView> getReceipts(LocalDate startDate, LocalDate endDate);

    ReceiptRecordView decreaseReceiptRecord(ReceiptRecordView receiptRecordView, double quantity);

}
