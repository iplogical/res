package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.entity.Restaurant;
import com.inspirationlogical.receipt.model.entity.Table;
import com.inspirationlogical.receipt.model.enums.TableType;

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

    public List<TableAdapter> getDisplayableTables() {
        Collection<Table> tables = adaptee.getTable();
        return tables.stream()
                .filter(table -> table.getType().equals(TableType.NORMAL) ||
                        table.getType().equals(TableType.VIRTUAL))
                .map(table -> new TableAdapter(table, manager))
                .collect(Collectors.toList());
    }
}
