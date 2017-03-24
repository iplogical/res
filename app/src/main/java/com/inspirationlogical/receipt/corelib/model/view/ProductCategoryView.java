package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;

public interface ProductCategoryView extends AbstractView {

    String getCategoryName();

    List<ProductCategoryView> getChildrenCategories();

    List<ProductView> getAllProducts();

}
