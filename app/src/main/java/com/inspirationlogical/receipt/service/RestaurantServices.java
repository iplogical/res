package com.inspirationlogical.receipt.service;

import java.util.List;

import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.model.view.RestaurantView;
import com.inspirationlogical.receipt.model.view.TableView;

import javafx.geometry.Point2D;

public interface RestaurantServices {

    RestaurantView getActiveRestaurant();

    List<TableView> getTables(RestaurantView restaurant);

    void setTableName(TableView table, String name);

    void setTableCapacity(TableView table, int capacity);

    void addTableNote(TableView table, String note);

    void displayTable(TableView table);

    void hideTable(TableView table);

    void moveTable(TableView table, Point2D position);

    TableView addTable(RestaurantView restaurant, TableType type, int tableNumber);

    ReceiptRecordView getActiveReceipt(TableView table);

}
