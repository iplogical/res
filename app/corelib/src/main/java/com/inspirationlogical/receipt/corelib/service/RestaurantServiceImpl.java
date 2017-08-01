package com.inspirationlogical.receipt.corelib.service;

import static com.inspirationlogical.receipt.corelib.model.enums.TableType.canBeHosted;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.adapter.*;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.ReservationViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.model.view.TableViewImpl;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;

import com.inspirationlogical.receipt.corelib.params.TableParams;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;


public class RestaurantServiceImpl extends AbstractService implements RestaurantService {

    @Inject
    public RestaurantServiceImpl(EntityManager manager) {
        super(manager);
    }

    @Override
    public RestaurantView getActiveRestaurant() {
        return new RestaurantViewImpl(RestaurantAdapter.getActiveRestaurant());
    }

    @Override
    public ReceiptView getOpenReceipt(TableView tableView) {
        return new ReceiptViewImpl(getTableAdapter(tableView).getOpenReceipt());
    }

    @Override
    public List<TableView> getTables() {
        return createViewsFromAdapters(TableAdapter.getDisplayableTables(), TableViewImpl::new);
    }

    @Override
    public int getFirstUnusedNumber() {
        return TableAdapter.getFirstUnusedNumber();
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableBuilder builder) {
        return new TableViewImpl(getRestaurantAdapter(restaurant).addTable(builder));
    }

    @Override
    public void deleteTable(TableView tableView) {
        getTableAdapter(tableView).deleteTable();
    }

    @Override
    public void mergeTables(TableView consumer, List<TableView> consumed) {
        TableAdapter consumerTableAdapter = getTableAdapter(consumer);
        List<TableAdapter> consumedTableAdapters = consumed.stream()
                .map(this::getTableAdapter)
                .collect(toList());

        getRestaurantAdapter(getActiveRestaurant()).mergeTables(consumerTableAdapter, consumedTableAdapters);
    }

    @Override
    public List<TableView> splitTables(TableView consumer) {
        TableAdapter consumerTableAdapter = getTableAdapter(consumer);

        return getRestaurantAdapter(getActiveRestaurant()).splitTables(consumerTableAdapter)
                .stream()
                .map(TableViewImpl::new)
                .collect(toList());
    }

    @Override
    public TableBuilder tableBuilder() {
        return Table.builder();
    }

    @Override
    public void setTableNumber(TableView tableView, int tableNumber, RestaurantView restaurant) {
        TableAdapter tableAdapter = getTableAdapter(tableView);
        if(getRestaurantAdapter(restaurant).isTableNumberAlreadyInUse(tableNumber)) {
            if(canBeHosted(tableView.getType())) {
                tableAdapter.setHost(tableNumber);
            } else {
                throw new IllegalTableStateException("The table number " + tableView.getNumber() + " is already in use");
            }
            return;
        }
        tableAdapter.removePreviousHost();
        tableAdapter.setNumber(tableNumber);
    }

    @Override
    public void setTableParams(TableView tableView, TableParams tableParams) {
        getTableAdapter(tableView).setTableParams(tableParams);
    }

    @Override
    public void setGuestCount(TableView tableView, int guestCount) {
        getTableAdapter(tableView).setGuestCount(guestCount);
    }

    @Override
    public void setTablePosition(TableView tableView, Point2D position) {
        getTableAdapter(tableView).setPosition(position);
    }

    @Override
    public void rotateTable(TableView tableView) {
        getTableAdapter(tableView).rotateTable();
    }

    @Override
    public void closeDay() {
        StockAdapter.closeLatestStockEntries();
        ReceiptAdapter.deleteReceipts();
        DailyClosureAdapter.getOpenDailyClosure().close();
    }

    @Override
    public List<ReservationView> getReservations() {
        return ReservationAdapter.getReservations()
                .stream()
                .map(ReservationViewImpl::new)
                .collect(toList());
    }

    @Override
    public List<ReservationView> getReservations(LocalDate date) {
        return ReservationAdapter.getReservationsByDate(date)
                .stream()
                .map(ReservationViewImpl::new)
                .collect(toList());
    }

    @Override
    public long addReservation(ReservationParams params) {
        return ReservationAdapter.addReservation(params);
    }

    @Override
    public void updateReservation(ReservationView selectedReservation, ReservationParams reservationParams) {
        getReservationAdapter(selectedReservation).updateReservation(reservationParams);
    }

    @Override
    public void updateReservation(long reservationId, ReservationParams reservationParams) {
        ReservationAdapter.getReservationById(reservationId).updateReservation(reservationParams);
    }

    @Override
    public void deleteReservation(long reservationId) {
        ReservationAdapter.getReservationById(reservationId).deleteReservation();
    }
}
