package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

/**
 * Created by Bálint on 2017.03.28..
 */
public interface PaymentViewController extends AbstractRetailViewController {

    void setSaleViewController(SaleViewController saleViewController);
}
