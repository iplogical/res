package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import javafx.scene.input.MouseEvent;

/**
 * Created by Bálint on 2017.03.24..
 */
public class SaleProductControllerImpl extends SaleElementControllerImpl<ProductView> {

    @Inject
    public SaleProductControllerImpl(SaleController saleController) {
        super(saleController);
    }

    @Override
    public void onElementClicked(MouseEvent event) {
        // TODO: Implement selling multiple products.
        saleController.sellProduct(view);
    }
}


