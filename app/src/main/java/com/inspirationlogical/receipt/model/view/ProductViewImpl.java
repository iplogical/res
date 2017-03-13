package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.ProductAdapter;

public class ProductViewImpl extends AbstractModelViewImpl<ProductAdapter>
        implements ProductView {

    private String name;

    public ProductViewImpl(ProductAdapter adapter) {
        super(adapter);
    }
}
