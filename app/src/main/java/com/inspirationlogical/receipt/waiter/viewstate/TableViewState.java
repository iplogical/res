package com.inspirationlogical.receipt.waiter.viewstate;

import com.inspirationlogical.receipt.corelib.model.view.TableView;

/**
 * Created by Bálint on 2017.03.22..
 */
public class TableViewState implements ViewState {

    private RestaurantViewState restaurantViewState;

    private TableView tableView;

    private boolean selected;

    public TableViewState(RestaurantViewState restaurantViewState, TableView tableView) {
        this.restaurantViewState = restaurantViewState;
        this.tableView = tableView;
    }

    @Override
    public boolean isConfigurable() {
        return restaurantViewState.isConfigurable();
    }

    @Override
    public boolean isFullScreen() {
        return restaurantViewState.isFullScreen();
    }

    public boolean isOpen() {
        return tableView.isOpen();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
