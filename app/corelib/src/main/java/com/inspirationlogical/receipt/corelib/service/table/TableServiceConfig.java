package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Point2D;

import java.util.List;

public interface TableServiceConfig {

    List<TableView> getDisplayableTables();

    TableView addTable(RestaurantView restaurantView, TableParams tableParams);

    void deleteTable(TableView tableView);

    void openTable(TableView tableView);

    boolean isTableOpen(TableView tableView);

    boolean reOpenTable(TableView tableView);

    TableView setTableParams(int tableNumber, TableParams tableParams);

    TableView setGuestCount(int tableNumber, int guestCount);

    TableView setPosition(int tableNumber, Point2D position);

    TableView rotateTable(int tableNumber);

    boolean isTableNumberAlreadyInUse(int tableNumber);

    void exchangeTables(TableView selectedView, TableView otherView);

    RecentConsumption getRecentConsumption(TableView tableView);
}
