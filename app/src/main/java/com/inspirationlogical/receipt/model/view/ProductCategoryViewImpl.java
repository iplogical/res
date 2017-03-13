package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.AbstractAdapter;
import com.inspirationlogical.receipt.model.adapter.ProductCategoryAdapter;

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
