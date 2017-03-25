package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;
import javafx.scene.input.MouseEvent;

/**
 * Created by Bálint on 2017.03.24..
 */
public class SaleViewCategoryControllerImpl extends SaleViewElementControllerImpl<ProductCategoryView> {

    private boolean isSelected;

    @Inject
    public SaleViewCategoryControllerImpl(SaleViewController saleViewController) {
        super(saleViewController);
    }

    @Override
    public void select(boolean isSelected) {
        this.isSelected = isSelected;
        CSSUtilities.setBorderColor(isSelected, vBox);
    }

    @Override
    public void onElementClicked(MouseEvent event) {
        if(isSelected) {
            return;
        } else {
            saleViewController.selectCategory(this);
        }


    }
}
