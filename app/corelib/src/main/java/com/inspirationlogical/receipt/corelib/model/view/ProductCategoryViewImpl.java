package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

import java.util.List;
import java.util.stream.Collectors;

public class ProductCategoryViewImpl extends AbstractModelViewImpl<ProductCategoryAdapter>
        implements ProductCategoryView {

    public ProductCategoryViewImpl(ProductCategoryAdapter adapter) {
        super(adapter);
    }


    @Override
    public String getCategoryName() {
        return adapter.getAdaptee().getName();
    }

    @Override
    public ProductCategoryView getParent() {
        return new ProductCategoryViewImpl(adapter.getParent());
    }

    @Override
    public List<ProductCategoryView> getChildrenCategories() {
        return adapter.getChildrenCategories().stream()
                .map(category -> new ProductCategoryViewImpl(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductView> getAllProducts() {
        return adapter.getAllProducts().stream()
                .map(productAdapter -> new ProductViewImpl(productAdapter))
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryType getType() {
        return adapter.getType();
    }

    @Override
    public String getName() {
        return adapter.getAdaptee().getName();
    }
}
