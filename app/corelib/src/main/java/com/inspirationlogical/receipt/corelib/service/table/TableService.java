package com.inspirationlogical.receipt.corelib.service.table;

import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.params.TableParams;

import java.util.List;

public interface TableService {

    TableAdapter addTable(RestaurantView restaurantView, TableParams tableParams);

    boolean isTableNumberAlreadyInUse(int tableNumber);

    void mergeTables(TableAdapter consumer, List<TableAdapter> consumed);

    List<TableAdapter> splitTables(TableAdapter consumer);
}
