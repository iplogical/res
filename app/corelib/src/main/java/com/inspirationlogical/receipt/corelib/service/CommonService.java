package com.inspirationlogical.receipt.corelib.service;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Product.ProductBuilder;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier.PriceModifierBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.StockView;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

public interface CommonService {
    ProductBuilder productBuilder();

    PriceModifierBuilder priceModifierBuilder();

    ProductView addProduct(ProductCategoryView parent, ProductBuilder builder);

    ProductCategoryView addProductCategory(ProductCategoryParams params);

    ProductCategoryView updateProductCategory(ProductCategoryParams params);

    void updateStock(List<StockParams> params, ReceiptType receiptType);

    void addPriceModifier(PriceModifierParams params);

    ProductCategoryView getRootProductCategory();

    List<ProductCategoryView> getAllCategories();

    List<ProductCategoryView> getAggregateCategories();

    List<ProductCategoryView> getLeafCategories();

    List<ProductView> getSellableProducts(ProductCategoryView category);

    List<StockView> getStockItems();

    List<PriceModifierView> getPriceModifiers();
}
