package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProductCategoryView implements AbstractView {

    public static ProductCategoryView getBackButtonCategoryView(String backButtonText) {
        return new ProductCategoryView(backButtonText);
    }

    private int id;
    private String categoryName;
    private int orderNumber;
    private ProductCategoryView parent;
    private ProductView product;
    private ProductCategoryType type;
    private ProductStatus status;

    private ProductCategoryView(String categoryName) {
        this.categoryName = categoryName;
    }

    public ProductCategoryView(ProductCategory productCategory) {
        id = productCategory.getId();
        categoryName = productCategory.getName();
        orderNumber = productCategory.getOrderNumber();
        parent = productCategory.getParent() == null ? null : new ProductCategoryView(productCategory.getParent());
        product = initProduct(productCategory);
        type = productCategory.getType();
        status = productCategory.getStatus();
    }

    private ProductView initProduct(ProductCategory productCategory) {
        if(productCategory.getProduct() != null) {
            return new ProductView(productCategory.getProduct());
        }
        return null;
    }

    @Override
    public String getName() {
        return categoryName;
    }
}
