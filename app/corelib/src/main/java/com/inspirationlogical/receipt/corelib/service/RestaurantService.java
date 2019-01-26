package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Point2D;

import java.util.List;

public interface RestaurantService {

    RestaurantView getActiveRestaurant();

    ReceiptView getOpenReceipt(TableView tableView);

    List<TableView> getDisplayableTables();

    int getFirstUnusedNumber();

    TableView addTable(RestaurantView restaurant, TableParams tableParams);

    void deleteTable(TableView tableView);

    TableView rotateTable(int tableNumber);

    List<TableView> exchangeTables(int selectedTableNumber, int otherTableNumber);

    TableView updateTableParams(int number, TableParams tableParams);

    TableView setGuestCount(int tableNumber, int guestCount);

    TableView setTablePosition(int tableNumber, Point2D position);

    void closeDay();
}
