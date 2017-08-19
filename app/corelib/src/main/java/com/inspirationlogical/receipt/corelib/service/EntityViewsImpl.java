package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ProductViewImpl;

import java.util.List;

import static com.inspirationlogical.receipt.corelib.service.AbstractService.createViewsFromAdapters;

public class EntityViewsImpl implements EntityViews {

    private static List<ProductView> productViews;

    private static List<ProductCategoryView> categoryViews;

    @Override
    public List<ProductView> getProductViews() {
        if(productViews == null) {
            productViews = createViewsFromAdapters(ProductAdapter.getActiveProducts(), ProductViewImpl::new);
        }
        return productViews;
    }

    @Override
    public List<ProductCategoryView> getCategoryViews() {
        if(categoryViews == null) {
            categoryViews = createViewsFromAdapters(ProductCategoryAdapter.getProductCategories(), ProductCategoryViewImpl::new);
        }
        return categoryViews;
    }
}
