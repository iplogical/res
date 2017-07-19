package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;

public interface VisibleProductController {
    void initCategories();

    void updateCategories();

    void selectCategory(SaleElementController saleElementController);

    void search(String text);
}
