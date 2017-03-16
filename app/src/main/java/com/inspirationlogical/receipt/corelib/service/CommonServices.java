package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import java.util.List;

public interface CommonServices {

    List<ProductView> getProducts(ProductCategoryView category);
}
