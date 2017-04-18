package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;

public interface ProductCategoryView extends AbstractView {

    String getCategoryName();

    ProductCategoryView getParent();

    List<ProductCategoryView> getChildrenCategories();

    List<ProductView> getAllProducts();

    List<ProductView> getAllActiveProducts();

    List<ProductView> getAllNormalProducts();

    ProductCategoryType getType();

    ProductStatus getStatus();
}
