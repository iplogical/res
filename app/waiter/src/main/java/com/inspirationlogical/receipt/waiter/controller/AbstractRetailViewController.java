package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

/**
 * Created by Bálint on 2017.03.28..
 */
public interface AbstractRetailViewController extends Controller {

    void setTableView(TableView tableView);
}
