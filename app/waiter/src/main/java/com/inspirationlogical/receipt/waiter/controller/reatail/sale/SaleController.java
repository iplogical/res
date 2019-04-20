package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailController;

public interface SaleController extends AbstractRetailController {

    void sellProduct(ProductView productView);

    void sellAdHocProduct(AdHocProductParams adHocProductParams);

    void hideAdHocProductForm();

    void enterSaleView();

    void clearSearch();

    void setTableView(TableView tableView);
}
