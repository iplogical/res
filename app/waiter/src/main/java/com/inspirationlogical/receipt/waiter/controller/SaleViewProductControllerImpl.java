package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import javafx.scene.input.MouseEvent;

/**
 * Created by Bálint on 2017.03.24..
 */
public class SaleViewProductControllerImpl extends SaleViewElementControllerImpl<ProductView> {

    @Inject
    public SaleViewProductControllerImpl(SaleViewController saleViewController) {
        super(saleViewController);
    }

    @Override
    public void onElementClicked(MouseEvent event) {
        // TODO: Implement selling multiple products.
        saleViewController.sellProduct(view);
    }
}


