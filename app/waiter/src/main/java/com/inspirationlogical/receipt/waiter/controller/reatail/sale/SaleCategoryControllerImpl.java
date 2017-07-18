package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;

import javafx.scene.input.MouseEvent;

/**
 * Created by Bálint on 2017.03.24..
 */
public class SaleCategoryControllerImpl extends SaleElementControllerImpl<ProductCategoryView> {

    private boolean isSelected;

    @Inject
    public SaleCategoryControllerImpl(SaleController saleController) {
        super(saleController);
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
            root.requestFocus();
            saleController.clearSearch();
            saleController.selectCategory(this);
        }
    }
}
