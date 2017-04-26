package com.inspirationlogical.receipt.corelib.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.adapter.*;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.*;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import static java.util.stream.Collectors.toList;


public class RestaurantServiceImpl extends AbstractService implements RestaurantService {

    private List<TableView> tableViews;

    @Inject
    public RestaurantServiceImpl(EntityManager manager) {
        super(manager);
        tableViews = TableAdapter.getTables();
    }

    @Override
    public TableBuilder tableBuilder() {
        return Table.builder();
    }

    @Override
    public RestaurantView getActiveRestaurant() {
        return new RestaurantViewImpl(RestaurantAdapter.restaurantAdapterFactory(manager));
    }

    @Override
    public List<TableView> getTables(RestaurantView restaurant) {
        return createViewsFromAdapters(getRestaurantAdapter(restaurant).getDisplayableTables(), TableViewImpl::new);
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
    public ReceiptView getActiveReceipt(TableView tableView) {
        return new ReceiptViewImpl(getTableAdapter(tableView).getActiveReceipt());
    }

    @Override
    public void setTableNumber(TableView tableView, int tableNumber, RestaurantView restaurant) {
        getRestaurantAdapter(restaurant).checkTableNumberCollision(tableNumber);
        getTableAdapter(tableView).setNumber(tableNumber);
    }

    @Override
    public void setTableType(TableView tableView, TableType tableType) {
        getTableAdapter(tableView).setType(tableType);
    }

    @Override
    public void setTableName(TableView tableView, String name) {
        getTableAdapter(tableView).setName(name);
    }

    @Override
    public void setTableCapacity(TableView tableView, int tableCapacity) {
        getTableAdapter(tableView).setCapacity(tableCapacity);
    }

    @Override
    public void setGuestCount(TableView tableView, int guestCount) {
        getTableAdapter(tableView).setGuestCount(guestCount);
    }

    @Override
    public void addTableNote(TableView tableView, String note) {
        getTableAdapter(tableView).setNote(note);
    }

    @Override
    public void displayTable(TableView tableView) {
        getTableAdapter(tableView).displayTable();
    }

    @Override
    public void hideTable(TableView tableView) {
        getTableAdapter(tableView).hideTable();
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
    public void setTableDimension(TableView tableView,Dimension2D dimension) {
        getTableAdapter(tableView).setDimension(dimension);
    }

    @Override
    public void deleteTable(TableView tableView) {
        getTableAdapter(tableView).deleteTable();
    }

    @Override
    public void mergeTables(TableView aggregate, List<TableView> consumed) {
        TableAdapter aggregateTableAdapter = getTableAdapter(aggregate);
        List<TableAdapter> consumedTableAdapters = consumed.stream()
                .map(this::getTableAdapter)
                .collect(toList());

        getRestaurantAdapter(getActiveRestaurant()).mergeTables(aggregateTableAdapter, consumedTableAdapters);
    }

    @Override
    public List<TableView> splitTables(TableView aggregate) {
        TableAdapter aggregateTableAdapter = getTableAdapter(aggregate);

        return getRestaurantAdapter(getActiveRestaurant()).splitTables(aggregateTableAdapter)
                .stream()
                .map(TableViewImpl::new)
                .collect(toList());
    }

    @Override
    public void closeDay() {
        StockAdapter.closeLatestStockEntries();
        DailyClosureAdapter.getOpenDailyClosure().close();
    }

    @Override
    public List<ReservationView> getReservations(LocalDate date) {
        return ReservationAdapter.getReservationsByDate(date)
                .stream()
                .map(ReservationViewImpl::new)
                .collect(toList());
    }
}
