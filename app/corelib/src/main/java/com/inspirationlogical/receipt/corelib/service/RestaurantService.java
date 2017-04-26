package com.inspirationlogical.receipt.corelib.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

public interface RestaurantService {
    TableBuilder tableBuilder();

    RestaurantView getActiveRestaurant();

    List<TableView> getTables(RestaurantView restaurant);

    int getFirstUnusedNumber();

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

    void setTablePosition(TableView tableView, Point2D position);

    void setTableDimension(TableView tableView,Dimension2D dimension);

    void rotateTable(TableView tableView);

    void deleteTable(TableView tableView);

    void mergeTables(TableView aggregate, List<TableView> consumed);

    List<TableView> splitTables(TableView aggregate);

    void closeDay();

    List<ReservationView> getReservations(LocalDate date);

    void addReservation(ReservationParams params);
}
