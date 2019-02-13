package com.inspirationlogical.receipt.corelib.service.product_category;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;

public interface ProductCategoryService {
    void addProductCategory(ProductCategoryParams params);

    void updateProductCategory(ProductCategoryParams params);

    void deleteProductCategory(String name);

    PriceModifier getPriceModifier(ProductCategory category, ReceiptRecord receiptRecord);
}
