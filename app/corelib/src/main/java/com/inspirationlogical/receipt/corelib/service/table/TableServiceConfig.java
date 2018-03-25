package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;

import java.util.List;

public interface TableServiceConfig {

    Table addTable(RestaurantView restaurantView, TableParams tableParams);

    void deleteTable(TableView tableView);

    boolean isTableNumberAlreadyInUse(int tableNumber);

    void mergeTables(TableAdapter consumer, List<TableAdapter> consumed);

    List<TableAdapter> splitTables(TableAdapter consumer);

    int getTotalPrice(TableView tableView);

    RecentConsumption getRecentConsumption(TableView tableView);
}
