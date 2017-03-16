package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;

public class ProductViewImpl extends AbstractModelViewImpl<ProductAdapter>
        implements ProductView {

    private String name;

    public ProductViewImpl(ProductAdapter adapter) {
        super(adapter);
    }
}
