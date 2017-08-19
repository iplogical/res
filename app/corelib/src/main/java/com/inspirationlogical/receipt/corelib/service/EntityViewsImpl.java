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
    public void initEntityViews() {
        productViews = createViewsFromAdapters(ProductAdapter.getActiveProducts(), ProductViewImpl::new);
        categoryViews = createViewsFromAdapters(ProductCategoryAdapter.getProductCategories(), ProductCategoryViewImpl::new);
    }

    @Override
    public List<ProductView> getProductViews() {
        if(productViews == null) {
            initEntityViews();
        }
        return productViews;
    }

    @Override
    public List<ProductCategoryView> getCategoryViews() {
        if(categoryViews == null) {
            initEntityViews();
        }
        return categoryViews;
    }
}
