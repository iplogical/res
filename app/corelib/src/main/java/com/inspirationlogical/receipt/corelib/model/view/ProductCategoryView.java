package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;

public interface ProductCategoryView extends AbstractView {

    String getCategoryName();

    ProductCategoryView getParent();

    ProductView getProduct();

    ProductCategoryType getType();

    ProductStatus getStatus();
}
