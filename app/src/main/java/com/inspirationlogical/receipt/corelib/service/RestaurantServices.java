package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import javafx.geometry.Point2D;

import java.util.List;

public interface RestaurantServices {
    TableBuilder tableBuilder();

    RestaurantView getActiveRestaurant();

    List<TableView> getTables(RestaurantView restaurant);

    void setTableName(TableView table, String name);

    void setTableCapacity(TableView table, int capacity);

    void addTableNote(TableView table, String note);

    void displayTable(TableView table);

    void hideTable(TableView table);

    void moveTable(TableView table, Point2D position);

    TableView addTable(RestaurantView restaurant, TableType type, int tableNumber);

    TableView addTable(RestaurantView restaurant, TableBuilder builder);

    ReceiptView getActiveReceipt(TableView table);

}
