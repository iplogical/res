package com.inspirationlogical.receipt.service;

import com.inspirationlogical.receipt.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.model.view.ProductView;

import java.util.List;

public interface CommonServices {

    List<ProductView> getProducts(ProductCategoryView category);
}
