package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import javafx.scene.input.MouseEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Bálint on 2017.03.24..
 */
public class ProductControllerImpl extends ElementControllerImpl<ProductView> {

    @Autowired
    public ProductControllerImpl(SaleController saleController) {
        super(saleController);
    }

    @Override
    public void onElementClicked(MouseEvent event) {
        saleController.sellProduct(view);
    }
}


