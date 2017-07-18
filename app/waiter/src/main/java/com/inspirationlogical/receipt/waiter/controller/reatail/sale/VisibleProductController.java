package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;

public interface VisibleProductController {
    void updateCategories();

    void upWithCategories();

    void selectCategory(SaleElementController saleElementController);
}
