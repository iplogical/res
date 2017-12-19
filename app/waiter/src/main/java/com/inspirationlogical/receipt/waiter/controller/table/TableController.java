package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.Control;

public interface TableController extends Controller {

    void setView(TableView tableView);

    void consumeTables();

    TableView getView();

    Control getRoot();

    ViewState getViewState();

    void updateTable();

    void releaseConsumedTables();

    void openTable(Control control);

    void deselectTable();

    void setOrderDelivered(boolean delivered);
}
