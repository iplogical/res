package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Point2D;

import java.time.LocalDateTime;
import java.util.List;

public interface TableServiceConfig {

    List<TableView> getDisplayableTables();

    TableView addTable(RestaurantView restaurantView, TableParams tableParams);

    void deleteTable(TableView tableView);

    TableView openTable(int tableNumber);

    boolean isTableOpen(TableView tableView);

    TableView reOpenTable(int tableNumber);

    TableView updateTableParams(int tableNumber, TableParams tableParams);

    TableView setGuestCount(int tableNumber, int guestCount);

    TableView setPosition(int tableNumber, Point2D position);

    TableView rotateTable(int tableNumber);

    List<TableView> exchangeTables(int selectedTableNumber, int otherTableNumber);

    RecentConsumption getRecentConsumption(TableView tableView);

    TableView setOrderDelivered(int tableNumber, boolean delivered);

    TableView setOrderDeliveredTime(int tableNumber, LocalDateTime now);
}
