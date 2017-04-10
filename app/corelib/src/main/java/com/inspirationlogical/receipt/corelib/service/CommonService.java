package com.inspirationlogical.receipt.corelib.service;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.StockView;

public interface CommonService {

    ProductCategoryView addProductCategory(ProductCategoryView parent, String name, ProductCategoryType type);

    ProductCategoryView getRootProductCategory();

    List<ProductCategoryView> getAggregateCategories();

    List<ProductView> getProducts(ProductCategoryView category);

    List<StockView> getStockItems();

    List<PriceModifierView> getPriceModifiers();
}
