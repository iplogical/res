package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.ProductCategoryAdapter;

public class ProductCategoryViewImpl implements ProductCategoryView {

    private ProductCategoryAdapter adapter;

    public ProductCategoryViewImpl(ProductCategoryAdapter adapter) {
        this.adapter = adapter;
    }

    public ProductCategoryAdapter getAdapter() {
        return adapter;
    }

    @Override
    public String getCategoryName() {
        return adapter.getAdaptee().getName();
    }
}
