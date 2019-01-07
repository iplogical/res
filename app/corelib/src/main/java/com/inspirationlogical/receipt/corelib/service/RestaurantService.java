package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Point2D;

import java.time.LocalDate;
import java.util.List;

public interface RestaurantService {

    RestaurantView getActiveRestaurant();

    ReceiptView getOpenReceipt(TableView tableView);

    List<TableView> getDisplayableTables();

    int getFirstUnusedNumber();

    TableView addTable(RestaurantView restaurant, TableParams tableParams);

    void deleteTable(TableView tableView);

    void rotateTable(TableView tableView);

    void exchangeTables(TableView selected, TableView other);

    void setTableNumber(TableView tableView, int tableNumber);

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
