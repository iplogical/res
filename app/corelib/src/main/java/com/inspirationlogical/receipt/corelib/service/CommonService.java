package com.inspirationlogical.receipt.corelib.service;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Product.ProductBuilder;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.StockView;

public interface CommonService {
    ProductBuilder productBuilder();

    ProductView addProduct(ProductCategoryView parent, ProductBuilder builder);

    ProductCategoryView addProductCategory(ProductCategoryParams params);

    ProductCategoryView updateProductCategory(ProductCategoryParams params);

    ProductCategoryView getRootProductCategory();

    List<ProductCategoryView> getAggregateCategories();

    List<ProductCategoryView> getLeafCategories();

    List<ProductView> getProducts(ProductCategoryView category);

    List<StockView> getStockItems();

    List<PriceModifierView> getPriceModifiers();
}
