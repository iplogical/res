package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import javafx.scene.control.Control;

import java.time.LocalDateTime;

public interface TableController extends Controller {

    void setView(TableView tableView);

    void consumeTables();

    TableView getView();

    Control getRoot();

    ViewState getViewState();

    void updateTable();

    void releaseConsumedTables();

    void openTable(Control control);

    void reOpenTable(Control control);

    void deselectTable();

    void setOrderDelivered(boolean delivered);

    LocalDateTime getOrderDeliveredTime();
}
