package com.inspirationlogical.receipt.waiter.viewstate;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
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

    public boolean isOpen() {
        return tableView.isOpen();
    }

    public boolean isVisible() {
        return tableView.isVisible();
    }

    public boolean isNormal() {
        return tableView.isNormal();
    }

    public boolean isAggregate() {
        return tableView.isAggregate();
    }

    public boolean isConsumed() {
        return tableView.isConsumed();
    }

    public boolean isLoiterer() {
        return tableView.isLoiterer();
    }

    public boolean isFrequenter() {
        return tableView.isFrequenter();
    }

    public boolean isEmployee() {
        return tableView.isEmployee();
    }
}
