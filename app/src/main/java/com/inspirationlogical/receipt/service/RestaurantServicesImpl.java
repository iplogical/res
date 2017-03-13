package com.inspirationlogical.receipt.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.model.adapter.RestaurantAdapter;
import com.inspirationlogical.receipt.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.model.entity.Restaurant;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.model.view.RestaurantView;
import com.inspirationlogical.receipt.model.view.RestaurantViewImpl;
import com.inspirationlogical.receipt.model.view.TableView;
import com.inspirationlogical.receipt.model.view.TableViewImpl;

public class RestaurantServicesImpl extends AbstractServices implements RestaurantServices {

    @Inject
    public RestaurantServicesImpl(EntityManager manager) {
        super(manager);
    }

    @Override
    public RestaurantView getActiveRestaurant() {
        List<Restaurant> restaurantList = manager.createNamedQuery(Restaurant.GET_ACTIVE_RESTAURANT).getResultList();
        if (restaurantList.isEmpty()) {
            //throw new RestaurantNotFoundException();
            return null;
        }
        return new RestaurantViewImpl(new RestaurantAdapter(restaurantList.get(0), manager));
    }

    @Override
    public List<TableView> getTables(RestaurantView restaurant) {
        Collection<TableAdapter> tableAdapters =((RestaurantViewImpl)restaurant).getAdapter().getDisplayableTables();
        return tableAdapters.stream()
                .map(tableAdapter -> new TableViewImpl(tableAdapter))
                .collect(Collectors.toList());
    }

    @Override
    public void setTableName(TableView table, String name) {
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
    public void moveTable(TableView table, int coordinateX, int coordinateY) {
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableType type, int tableNumber) {
        return null;
    }

    @Override
    public ReceiptRecordView getActiveReceipt(TableView table) {
        return null;
    }

}
