package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

import java.util.List;

public interface ProductCategoryView extends AbstractView {

    String getCategoryName();

    ProductCategoryView getParent();

    List<ProductCategoryView> getChildrenCategories();

    List<ProductView> getAllProducts();

    ProductCategoryType getType();
}
