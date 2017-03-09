package com.inspirationlogical.receipt.service;

import java.util.List;

import com.inspirationlogical.receipt.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.model.view.ProductView;

public interface ProductService {

    List<ProductView> getProducts(ProductCategoryView category);
}
