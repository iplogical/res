package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.TableViewBuilder;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapter extends AbstractAdapter<Restaurant> {

    public static RestaurantAdapter restaurantAdapterFactory(EntityManager manager) {
        List<Restaurant> restaurantList = manager.createNamedQuery(Restaurant.GET_ACTIVE_RESTAURANT).getResultList();
        if (restaurantList.isEmpty()) {
            throw new RestaurantNotFoundException();
        }
        return new RestaurantAdapter(restaurantList.get(0), manager);
    }

    public RestaurantAdapter(Restaurant adaptee, EntityManager manager) {
        super(adaptee, manager);
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
                .visibility(true)
                .build();
        adaptee.getTable().add(newTable);
        newTable.setOwner(adaptee);
        GuardedTransaction.Run(manager, () -> manager.persist(adaptee));
        return new TableAdapter(newTable, manager);
    }

    public TableAdapter addTable(TableViewBuilder builder) {
        GuardedTransaction.Run(manager, () -> manager.refresh(adaptee));
        Table newTable = Table.builder()
                .type(builder.getType())
                .number(builder.getTableNumber())
                .name(builder.getName())
                .coordinateX((int)builder.getPosition().getX())
                .coordinateY((int)builder.getPosition().getY())
                .guestNumber(builder.getGuestNumber())
                .capacity(builder.getTableCapacity())
                .note(builder.getNote())
                .visibility(builder.isVisibility())
                .build();
        adaptee.getTable().add(newTable);
        newTable.setOwner(adaptee);
        GuardedTransaction.Run(manager, () -> manager.persist(adaptee));
        return new TableAdapter(newTable, manager);
    }
}
