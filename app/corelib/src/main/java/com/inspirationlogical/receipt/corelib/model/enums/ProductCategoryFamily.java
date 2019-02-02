package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;

public enum ProductCategoryFamily {

    FOOD,
    DRINK;

    public static ProductCategoryFamily initFamily(Product product) {
        ProductCategory pseudo = product.getCategory();
        ProductCategory category = pseudo.getParent();
        while (!category.getParent().getType().equals(ProductCategoryType.ROOT)) {
            category = category.getParent();
        }
        return category.getFamily();
    }
}
