package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;

public interface TableController extends Initializable {

    TableView getView();

    Control getRoot();

    ViewState getViewState();

    void updateNode();

    void showConfigureTableForm(Control control);

    void configureTable(String name, int guestNumber);

    void openTable(Control control);
}
