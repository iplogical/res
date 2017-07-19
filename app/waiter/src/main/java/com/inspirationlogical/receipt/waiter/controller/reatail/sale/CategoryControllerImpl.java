package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;

import javafx.scene.input.MouseEvent;

/**
 * Created by Bálint on 2017.03.24..
 */
public class CategoryControllerImpl extends ElementControllerImpl<ProductCategoryView> {

    private boolean isSelected;

    @Inject
    public CategoryControllerImpl(SaleController saleController) {
        super(saleController);
    }

    @Override
    public void select() {
        this.isSelected = true;
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
