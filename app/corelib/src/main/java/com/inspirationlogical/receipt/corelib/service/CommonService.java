package com.inspirationlogical.receipt.corelib.service;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.StockView;

public interface CommonService {

    ProductCategoryView getRootProductCategory();

    List<ProductView> getProducts(ProductCategoryView category);

    List<StockView> getStockItems();
}
