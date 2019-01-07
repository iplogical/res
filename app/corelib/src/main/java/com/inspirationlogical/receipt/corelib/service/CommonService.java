package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.entity.Product.ProductBuilder;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import java.util.List;

public interface CommonService {
    ProductBuilder productBuilder();

    ProductCategoryView getRootProductCategory();

    List<ProductCategoryView> getAggregateCategories();

    List<ProductCategoryView> getLeafCategories();

    List<ProductCategoryView> getChildCategories(ProductCategoryView productCategoryView);

    List<ProductView> getActiveProducts();

    List<ProductView> getSellableProducts();

    List<ProductView> getSellableProducts(ProductCategoryView productCategoryView);

    List<ProductView> getStorableProducts();
}
