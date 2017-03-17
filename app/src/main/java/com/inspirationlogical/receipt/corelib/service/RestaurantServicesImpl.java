package com.inspirationlogical.receipt.corelib.service;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.adapter.RestaurantAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import javafx.geometry.Point2D;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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
    public void setTableCapacity(TableView tableView, int capacity) {
        ((TableViewImpl)tableView).getAdapter().setCapacity(capacity);
    }

    @Override
    public void addTableNote(TableView tableView, String note) {
        ((TableViewImpl)tableView).getAdapter().setNote(note);
    }

    @Override
    public void displayTable(TableView tableView) {
        ((TableViewImpl)tableView).getAdapter().displayTable();
    }

    @Override
    public void hideTable(TableView tableView) {
        ((TableViewImpl)tableView).getAdapter().hideTable();
    }

    @Override
    public void moveTable(TableView tableView, Point2D position) {
        ((TableViewImpl)tableView).getAdapter().moveTable(position);
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableType type, int tableNumber) {
        RestaurantAdapter restaurantAdapter = ((RestaurantViewImpl)restaurant).getAdapter();
        return new TableViewImpl(restaurantAdapter.addTable(type, tableNumber));
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableViewBuilder builder) {
        RestaurantAdapter restaurantAdapter = ((RestaurantViewImpl)restaurant).getAdapter();
        return new TableViewImpl(restaurantAdapter.addTable(builder));
    }

    @Override
    public ReceiptView getActiveReceipt(TableView tableView) {
        return new ReceiptViewImpl(((TableViewImpl)tableView).getAdapter().getActiveReceipt());
    }
}
