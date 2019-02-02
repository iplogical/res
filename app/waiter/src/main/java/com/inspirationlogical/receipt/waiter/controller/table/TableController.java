package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.scene.control.Control;

import java.time.LocalDateTime;

public interface TableController extends Controller {

    TableView getTableView();

    void setTableView(TableView tableView);

    void initialize(TableView tableView);

    boolean isOpen();

    int getTableNumber();

    TableType getTableType();

    Control getRoot();

    void updateTable();

    void openTable(Control control);

    void reOpenTable(Control control);

    void deselectTable();

    void setFoodDelivered(boolean delivered);

    LocalDateTime getFoodDeliveryTime();

    void setDrinkDelivered(boolean delivered);

    LocalDateTime getDrinkDeliveryTime();

    boolean isSelected();

}
