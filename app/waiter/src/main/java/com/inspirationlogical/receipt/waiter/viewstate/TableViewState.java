package com.inspirationlogical.receipt.waiter.viewstate;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

import lombok.Data;

/**
 * Created by Bálint on 2017.03.22..
 */
@Data
public class TableViewState implements ViewState {

    private RestaurantViewState restaurantViewState;

    private TableView tableView;

    private boolean selected;

    public TableViewState(RestaurantViewState restaurantViewState, TableView tableView) {
        this.restaurantViewState = restaurantViewState;
        this.tableView = tableView;
    }

    public boolean isVirtual() {
        return tableView.isVirtual();
    }

    public boolean isOpen() {
        return tableView.isOpen();
    }
}
