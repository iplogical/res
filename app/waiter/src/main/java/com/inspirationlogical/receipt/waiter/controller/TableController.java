package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.Control;

public interface TableController extends Controller {

    void setView(TableView tableView);

    TableView getView();

    Control getRoot();

    ViewState getViewState();

    void updateNode();

    void setTable(String name, int guestCount, String note);

    void openTable(Control control);

    void deselectTable();
}
