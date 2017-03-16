package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.Initializable;
import javafx.scene.Node;

public interface TableController extends Initializable {

    TableView getViewData();

    Node getView();
}
