package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ProductCategoryViewImpl implements ProductCategoryView {

    private long id;
    private String name;
    private int orderNumber;
    private ProductCategoryView parent;
    private ProductView product;
    private ProductCategoryType type;
    private ProductStatus status;

    public ProductCategoryViewImpl(ProductCategory productCategory) {
        id = productCategory.getId();
        name = productCategory.getName();
        orderNumber = productCategory.getOrderNumber();
        parent = productCategory.getParent() == null ? null : new ProductCategoryViewImpl(productCategory.getParent());
        product = initProduct(productCategory);
        type = productCategory.getType();
        status = productCategory.getStatus();
    }

    private ProductView initProduct(ProductCategory productCategory) {
        if(productCategory.getProduct() != null) {
            return new ProductViewImpl(productCategory.getProduct());
        }
        return null;
    }

    @Override
    public String getCategoryName() {
        return name;
    }
}
