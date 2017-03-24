package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import javafx.scene.input.MouseEvent;

/**
 * Created by Bálint on 2017.03.24..
 */
public class SaleViewCategoryControllerImpl extends SaleViewElementControllerImpl<ProductCategoryView> {

    @Inject
    public SaleViewCategoryControllerImpl(SaleViewController saleViewController) {
        super(saleViewController);
    }

    @Override
    public void onElementClicked(MouseEvent event) {
        System.out.println("Clicked Category.");
    }
}
