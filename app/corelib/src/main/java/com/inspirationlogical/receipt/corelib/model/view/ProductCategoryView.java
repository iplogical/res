package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

public interface ProductCategoryView extends AbstractView {

    String getCategoryName();

    ProductCategoryView getParent();

    List<ProductCategoryView> getChildrenCategories();

    List<ProductView> getAllProducts();

    ProductCategoryType getType();
}
