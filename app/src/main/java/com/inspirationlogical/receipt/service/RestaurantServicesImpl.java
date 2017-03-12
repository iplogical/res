package com.inspirationlogical.receipt.service;

import java.util.List;

import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.view.RestaurantView;
import com.inspirationlogical.receipt.view.TableView;

public class RestaurantServicesImpl implements RestaurantServices {

    @Override
    public List<TableView> getTables() {
        return null;
    }

    @Override
    public void setTableName(TableView table, String name) {
    }

    @Override
    public void setTableCapacity(TableView table, int capacity) {
    }

    @Override
    public void addTableNote(TableView table, String note) {
    }

    @Override
    public void displayTable(TableView table) {
    }

    @Override
    public void hideTable(TableView table) {
    }

    @Override
    public void moveTable(TableView table, int coordinateX, int coordinateY) {
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableType type, int tableNumber) {
        return null;
    }

    @Override
    public ReceiptRecordView getActiveReceipt(TableView table) {
        return null;
    }

}
