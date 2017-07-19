package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailController;

/**
 * Created by Bálint on 2017.03.22..
 */
public interface SaleController extends AbstractRetailController {

    void sellProduct(ProductView productView);

    void sellAdHocProduct(AdHocProductParams adHocProductParams);

    void selectCategory(ElementController elementController);

    void enterSaleView();

    void clearSearch();
}
