package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.ProductAdapter;

public class ProductViewImpl implements ProductView {

    private ProductAdapter adapter;
    private String name;

    public ProductViewImpl(ProductAdapter adapter) {
        this.adapter = adapter;
    }
}
