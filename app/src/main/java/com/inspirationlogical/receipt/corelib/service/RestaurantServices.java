package com.inspirationlogical.receipt.corelib.service;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;

import javafx.geometry.Point2D;

public interface RestaurantServices {
    TableBuilder tableBuilder();

    RestaurantView getActiveRestaurant();

    List<TableView> getTables(RestaurantView restaurant);

    void setTableNumber(TableView tableView, int tableNumber);

    void setTableType(TableView tableView, TableType tableType);

    void setTableName(TableView tableView, String name);

    void setTableCapacity(TableView tableView, int tableCapacity);

    void addTableNote(TableView tableView, String note);

    void displayTable(TableView tableView);

    void hideTable(TableView tableView);

    void moveTable(TableView tableView, Point2D position);

    void deleteTable(TableView tableView);

    // TODO: remove this when not used by the UI.
    TableView addTable(RestaurantView restaurant, TableType type, int tableNumber);

    TableView addTable(RestaurantView restaurant, TableBuilder builder);

    ReceiptView getActiveReceipt(TableView tableView);
}
