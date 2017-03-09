package com.inspirationlogical.receipt.model.adapter;

import java.util.List;

import com.inspirationlogical.receipt.model.ProductCategory;

public interface ProductCategoryAdapter extends AbstractAdapter<ProductCategory> {

    public List<ProductAdapter> getAllProducts();
}
