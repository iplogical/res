package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter;

public class ProductCategoryViewImpl extends AbstractModelViewImpl<ProductCategoryAdapter>
        implements ProductCategoryView {

    public ProductCategoryViewImpl(ProductCategoryAdapter adapter) {
        super(adapter);
    }


    @Override
    public String getCategoryName() {
        return adapter.getAdaptee().getName();
    }
}
