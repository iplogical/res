package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;

public interface CategoryController {

    Control getRoot();

    void setView(ProductCategoryView view);

    ProductCategoryView getView();

    void select();

    void onCategoryClicked(MouseEvent event);

    void updateNode();
}
