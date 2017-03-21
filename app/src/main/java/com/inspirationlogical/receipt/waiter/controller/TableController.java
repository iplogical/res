package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.Initializable;
import javafx.scene.control.Control;

public interface TableController extends Initializable {

    TableView getView();

    Control getRoot();

    void updateNode();

    void showConfigureTableForm(Control control);

    void configureTable(String name, int guestNumber);
}
