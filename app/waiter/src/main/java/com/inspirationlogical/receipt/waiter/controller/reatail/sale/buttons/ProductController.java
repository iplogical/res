package com.inspirationlogical.receipt.waiter.controller.reatail.sale.buttons;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

public interface ProductController {

    Control getRoot();

    void setView(ProductView view);

    ProductView getView();

    void select();

    void onProductClicked(MouseEvent event);

    void updateNode();
}