package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.model.entity.Restaurant;
import com.inspirationlogical.receipt.model.entity.Table;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.utils.GuardedTransaction;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapter extends AbstractAdapter<Restaurant> {

    public RestaurantAdapter(Restaurant adaptee, EntityManager manager) {
        super(adaptee, manager);
    }

    public static RestaurantAdapter restaurantAdapterFactory(EntityManager manager) {
        List<Restaurant> restaurantList = manager.createNamedQuery(Restaurant.GET_ACTIVE_RESTAURANT).getResultList();
        if (restaurantList.isEmpty()) {
            throw new RestaurantNotFoundException();
        }
        return new RestaurantAdapter(restaurantList.get(0), manager);
    }

    public List<TableAdapter> getDisplayableTables() {
        GuardedTransaction.Run(manager, () -> manager.refresh(adaptee));
        Collection<Table> tables = adaptee.getTable();
        return tables.stream()
                .filter(table -> table.getType().equals(TableType.NORMAL) ||
                        table.getType().equals(TableType.VIRTUAL))
                .map(table -> new TableAdapter(table, manager))
                .collect(Collectors.toList());
    }

    public TableAdapter addTable(TableType type, int tableNumber) {
        GuardedTransaction.Run(manager, () -> manager.refresh(adaptee));
        Table newTable = Table.builder()
                .type(type)
                .number(tableNumber)
                .build();
        adaptee.getTable().add(newTable);
        newTable.setOwner(adaptee);
        GuardedTransaction.Run(manager, () -> manager.persist(adaptee));
        return new TableAdapter(newTable, manager);
    }
}
