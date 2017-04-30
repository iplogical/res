package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

public interface TableFormController extends Controller {
    void loadTable(TableController tableView, TableType tableType);
}
