package com.inspirationlogical.receipt.corelib.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.adapter.RestaurantAdapter;
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

public class RestaurantServicesImpl extends AbstractServices implements RestaurantServices {

    @Inject
    public RestaurantServicesImpl(EntityManager manager) {
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
        Collection<TableAdapter> tableAdapters =((RestaurantViewImpl)restaurant).getAdapter().getDisplayableTables();
        return tableAdapters.stream()
                .map(tableAdapter -> new TableViewImpl(tableAdapter))
                .collect(Collectors.toList());
    }

    @Override
    public void setTableNumber(TableView tableView, int tableNumber) {
        // todo: implement
    }

    @Override
    public void setTableType(TableView tableView, TableType tableType) {
        // todo: implement
    }

    @Override
    public void setTableName(TableView tableView, String name) {
        ((TableViewImpl)tableView).getAdapter().setTableName(name);
    }

    @Override
    public void setTableCapacity(TableView tableView, int tableCapacity) {
        ((TableViewImpl)tableView).getAdapter().setCapacity(tableCapacity);
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
    public void deleteTable(TableView tableView) {
        // todo: implement
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableType type, int tableNumber) {
        RestaurantAdapter restaurantAdapter = ((RestaurantViewImpl)restaurant).getAdapter();
        return new TableViewImpl(restaurantAdapter.addTable(type, tableNumber));
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableBuilder builder) {
        RestaurantAdapter restaurantAdapter = ((RestaurantViewImpl)restaurant).getAdapter();
        return new TableViewImpl(restaurantAdapter.addTable(builder));
    }

    @Override
    public ReceiptView getActiveReceipt(TableView tableView) {
        return new ReceiptViewImpl(((TableViewImpl)tableView).getAdapter().getActiveReceipt());
    }
}
