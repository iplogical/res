package com.inspirationlogical.receipt.corelib.service.product_category;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;

public interface ProductCategoryService {
    double getDiscount(ProductCategory category, ReceiptRecord receiptRecord);
}
