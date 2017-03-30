package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

/**
 * Created by Bálint on 2017.03.23..
 */
public interface SaleElementController<T extends AbstractView> extends Controller {

    void setView(T view);

    T getView();

    void select(boolean isSelected);

    @FXML
    void onElementClicked(MouseEvent event);
}