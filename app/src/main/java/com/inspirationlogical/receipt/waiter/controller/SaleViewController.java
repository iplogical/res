package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

/**
 * Created by Bálint on 2017.03.22..
 */
public interface SaleViewController extends Controller {

    void setTableView(TableView tableView);

    void sellProduct(ProductView productView);

    void selectCategory(SaleViewElementController saleViewElementController);
}
