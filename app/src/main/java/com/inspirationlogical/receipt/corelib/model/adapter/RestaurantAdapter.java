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

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantAdapter extends AbstractAdapter<Restaurant> {

    public static RestaurantAdapter restaurantAdapterFactory(EntityManager manager) {
        List<Restaurant> restaurantList = manager.createNamedQuery(Restaurant.GET_ACTIVE_RESTAURANT).getResultList();
        if (restaurantList.isEmpty()) {
            throw new RestaurantNotFoundException();
        }
        return new RestaurantAdapter(restaurantList.get(0));
    }

    public RestaurantAdapter(Restaurant adaptee) {
        super(adaptee);
    }


    public List<TableAdapter> getDisplayableTables() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        Collection<Table> tables = adaptee.getTable();
        return tables.stream()
                .filter(table -> table.getType().equals(TableType.NORMAL) ||
                        table.getType().equals(TableType.VIRTUAL))
                .map(table -> new TableAdapter(table))
                .collect(Collectors.toList());
    }

    public TableAdapter addTable(TableType type, int tableNumber) {
        final Table[] newTable = new Table[1];
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            newTable[0] = Table.builder()
                    .type(type)
                    .number(tableNumber)
                    .visibility(true)
                    .build();
            adaptee.getTable().add(newTable[0]);
            newTable[0].setOwner(adaptee);
        });
        return new TableAdapter(newTable[0]);
    }

    public TableAdapter addTable(Table.TableBuilder builder) {
        final Table[] newTable = new Table[1];
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            newTable[0] = builder.build();
            adaptee.getTable().add(newTable[0]);
            newTable[0].setOwner(adaptee);
            // FIXME(efereja): persist new Table?
        });
        return new TableAdapter(newTable[0]);
    }
}
