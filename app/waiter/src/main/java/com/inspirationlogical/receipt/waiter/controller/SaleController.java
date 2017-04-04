package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.AdHocProductParams;

/**
 * Created by Bálint on 2017.03.22..
 */
public interface SaleController extends AbstractRetailController {

    void sellProduct(ProductView productView);

    void sellAdHocProduct(AdHocProductParams adHocProductParams);

    void selectCategory(SaleElementController saleElementController);

    void upWithCategories();

    void updateNode();
}
