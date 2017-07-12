package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;

public interface TableFormController extends Controller {
    void loadTable(TableController tableView, TableType tableType);
}
