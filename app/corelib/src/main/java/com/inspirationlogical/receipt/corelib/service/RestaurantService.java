package com.inspirationlogical.receipt.corelib.service;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.geometry.Point2D;

public interface RestaurantService {
    TableBuilder tableBuilder();

    RestaurantView getActiveRestaurant();

    List<TableView> getTables(RestaurantView restaurant);

    TableView addTable(RestaurantView restaurant, TableBuilder builder);

    ReceiptView getActiveReceipt(TableView tableView);

    void setTableNumber(TableView tableView, int tableNumber, RestaurantView restaurant);

    void setTableType(TableView tableView, TableType tableType);

    void setTableName(TableView tableView, String name);

    void setGuestCount(TableView tableView, int guestCount);

    void setTableCapacity(TableView tableView, int tableCapacity);

    void addTableNote(TableView tableView, String note);

    void displayTable(TableView tableView);

    void hideTable(TableView tableView);

    void moveTable(TableView tableView, Point2D position);

    void rotateTable(TableView tableView);

    void deleteTable(TableView tableView);

    void mergeTables(TableView aggregate, List<TableView> consumed);

    void closeDay();
}
