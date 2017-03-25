package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.Control;

public interface TableController extends Controller {

    void setView(TableView tableView);

    TableView getView();

    Control getRoot();

    ViewState getViewState();

    void updateNode();

    void showTableSettingsForm(Control control);

    void setTable(String name, int guestNumber);

    void openTable(Control control);

    void deselectTable();
}
