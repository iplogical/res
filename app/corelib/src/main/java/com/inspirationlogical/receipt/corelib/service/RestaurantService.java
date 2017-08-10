package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.time.LocalDate;
import java.util.List;

public interface RestaurantService {

    RestaurantView getActiveRestaurant();

    ReceiptView getOpenReceipt(TableView tableView);

    List<TableView> getTables();

    int getFirstUnusedNumber();

    TableView addTable(RestaurantView restaurant, TableBuilder builder);

    void deleteTable(TableView tableView);

    void rotateTable(TableView tableView);

    void mergeTables(TableView consumer, List<TableView> consumed);

    List<TableView> splitTables(TableView consumer);

    void exchangeTables(List<TableView> tables);

    TableBuilder tableBuilder();

    void setTableNumber(TableView tableView, int tableNumber, RestaurantView restaurant);

    void setTableParams(TableView tableView, TableParams tableParams);

    void setGuestCount(TableView tableView, int guestCount);

    void setTablePosition(TableView tableView, Point2D position);

    void closeDay();

    List<ReservationView> getReservations();

    List<ReservationView> getReservations(LocalDate date);

    long addReservation(ReservationParams params);

    void updateReservation(ReservationView selectedReservation, ReservationParams reservationParams);

    void updateReservation(long reservationId, ReservationParams reservationParams);

    void deleteReservation(long reservationId);
}
