package com.inspirationlogical.receipt.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.model.adapter.RestaurantAdapter;
import com.inspirationlogical.receipt.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.model.view.RestaurantView;
import com.inspirationlogical.receipt.model.view.RestaurantViewImpl;
import com.inspirationlogical.receipt.model.view.TableView;
import com.inspirationlogical.receipt.model.view.TableViewImpl;

import javafx.geometry.Point2D;

public class RestaurantServicesImpl extends AbstractServices implements RestaurantServices {

    @Inject
    public RestaurantServicesImpl(EntityManager manager) {
        super(manager);
    }

    @Override
    public RestaurantView getActiveRestaurant() {
        return new RestaurantViewImpl(RestaurantAdapter.restaurantAdapterFactory(manager));
    }

    @Override
    public List<TableView> getTables(RestaurantView restaurant) {
        Collection<TableAdapter> tableAdapters =((RestaurantViewImpl)restaurant).getAdapter().getDisplayableTables();
        return tableAdapters.stream()
                .map(tableAdapter -> new TableViewImpl(tableAdapter))
                .collect(Collectors.toList());
    }

    @Override
    public void setTableName(TableView tableView, String name) {
        ((TableViewImpl)tableView).getAdapter().setTableName(name);
    }

    @Override
    public void setTableCapacity(TableView table, int capacity) {
    }

    @Override
    public void addTableNote(TableView table, String note) {
    }

    @Override
    public void displayTable(TableView table) {
    }

    @Override
    public void hideTable(TableView table) {
    }

    @Override
    public void moveTable(TableView table, Point2D position) {
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableType type, int tableNumber) {
        RestaurantAdapter restaurantAdapter = ((RestaurantViewImpl)restaurant).getAdapter();
        return new TableViewImpl(restaurantAdapter.addTable(type, tableNumber));
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableType type, int tableNumber, int tableCapacity, Point2D position) {

        return null;
    }

    @Override
    public ReceiptRecordView getActiveReceipt(TableView table) {
        return null;
    }

}
