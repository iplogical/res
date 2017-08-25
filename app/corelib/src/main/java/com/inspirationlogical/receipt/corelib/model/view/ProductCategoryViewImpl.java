package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;

public class ProductCategoryViewImpl extends AbstractModelViewImpl<ProductCategoryAdapter>
        implements ProductCategoryView {

    public ProductCategoryViewImpl(ProductCategoryAdapter adapter) {
        super(adapter);
    }

    @Override
    public String getName() {
        return adapter.getAdaptee().getName();
    }

    @Override
    public int getOrderNumber() {
        return adapter.getAdaptee().getOrderNumber();
    }

    @Override
    public String getCategoryName() {
        return adapter.getAdaptee().getName();
    }

    @Override
    public ProductCategoryView getParent() {
        return new ProductCategoryViewImpl(new ProductCategoryAdapter(adapter.getAdaptee().getParent()));
    }


    @Override
    public ProductView getProduct() {
        if(adapter.getAdaptee().getProduct() != null)
            return new ProductViewImpl(new ProductAdapter(adapter.getAdaptee().getProduct()));
        return null;
    }

    @Override
    public ProductCategoryType getType() {
        return adapter.getAdaptee().getType();
    }

    @Override
    public ProductStatus getStatus() {
        return adapter.getAdaptee().getStatus();
    }
}
