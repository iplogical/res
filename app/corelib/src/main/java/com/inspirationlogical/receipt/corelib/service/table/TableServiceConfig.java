package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Point2D;

import java.time.LocalDateTime;
import java.util.List;

public interface TableServiceConfig {

    List<TableView> getDisplayableTables();

    int getFirstUnusedNumber();

    TableView addTable(TableParams tableParams);

    void deleteTable(int tableNumber);

    TableView openTable(int tableNumber);

    boolean isTableOpen(int tableNumber);

    TableView reOpenTable(int tableNumber);

    TableView updateTableParams(int tableNumber, TableParams tableParams);

    TableView setGuestCount(int tableNumber, int guestCount);

    TableView setTablePosition(int tableNumber, Point2D position);

    TableView rotateTable(int tableNumber);

    List<TableView> exchangeTables(int selectedTableNumber, int otherTableNumber);

    RecentConsumption getRecentConsumption(int tableNumber);

    TableView setFoodDelivered(int tableNumber, boolean delivered);

    TableView setFoodDeliveryTime(int tableNumber, LocalDateTime now);

    TableView setDrinkDelivered(int tableNumber, boolean delivered);

    TableView setDrinkDeliveryTime(int tableNumber, LocalDateTime now);
}
