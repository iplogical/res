package com.inspirationlogical.receipt.service;

import java.util.List;

import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.view.RestaurantView;
import com.inspirationlogical.receipt.view.TableView;

public interface RestaurantServices {

    List<TableView> getTables();

    void setTableName(TableView table, String name);

    void setTableCapacity(TableView table, int capacity);

    void addTableNote(TableView table, String note);

    void displayTable(TableView table);

    void hideTable(TableView table);

    void moveTable(TableView table, int coordinateX, int coordinateY);

    TableView addTable(RestaurantView restaurant, TableType type, int tableNumber);

    ReceiptRecordView getActiveReceipt(TableView table);

}
