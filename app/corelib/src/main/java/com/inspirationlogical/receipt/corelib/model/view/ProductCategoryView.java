package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;

public interface ProductCategoryView extends AbstractView {

    default String getCategoryName() { return null;}

    default ProductCategoryView getParent() { return null;}

    default ProductView getProduct() { return null;}

    default ProductCategoryType getType() { return null;}

    default ProductStatus getStatus() { return null;}
}
