package com.inspirationlogical.receipt.corelib.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.adapter.RestaurantAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.entity.Table.TableBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantViewImpl;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.model.view.TableViewImpl;

import javafx.geometry.Point2D;

;

public class RestaurantServiceImpl extends AbstractService implements RestaurantService {

    @Inject
    public RestaurantServiceImpl(EntityManager manager) {
        super(manager);
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
        return getRestaurantAdapter(restaurant).getDisplayableTables().stream()
                .map(tableAdapter -> new TableViewImpl(tableAdapter))
                .collect(Collectors.toList());
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
    public void setTableNumber(TableView tableView, int tableNumber) {
        getTableAdapter(tableView).setTableNumber(tableNumber);
    }

    @Override
    public void setTableType(TableView tableView, TableType tableType) {
        getTableAdapter(tableView).setTableType(tableType);
    }

    @Override
    public void setTableName(TableView tableView, String name) {
        getTableAdapter(tableView).setTableName(name);
    }

    @Override
    public void setTableCapacity(TableView tableView, int tableCapacity) {
        getTableAdapter(tableView).setCapacity(tableCapacity);
    }

    @Override
    public void setGuestCount(TableView tableView, int guestNumber) {
        getTableAdapter(tableView).setGuestNumber(guestNumber);
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
    public void moveTable(TableView tableView, Point2D position) {
        getTableAdapter(tableView).moveTable(position);
    }

    @Override
    public void rotateTable(TableView tableView) {
        getTableAdapter(tableView).rotateTable();
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
                .collect(Collectors.toList());

        getRestaurantAdapter(getActiveRestaurant()).mergeTables(aggregateTableAdapter, consumedTableAdapters);
    }

    @Override
    public void closeDay() {
        StockAdapter.closeLatestStockEntries();
    }
}
