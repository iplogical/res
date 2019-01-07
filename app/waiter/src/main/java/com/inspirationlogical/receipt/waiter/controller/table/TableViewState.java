package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantViewState;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by Bálint on 2017.03.22..
 */
@Data
public class TableViewState implements ViewState {

    private RestaurantViewState restaurantViewState;

    private TableView tableView;

    private boolean isOpen;

    private boolean selected;

    private boolean orderDelivered;

    private LocalDateTime orderDeliveredTime;

    TableViewState(RestaurantViewState restaurantViewState, TableView tableView, boolean isOpen) {
        this.restaurantViewState = restaurantViewState;
        this.tableView = tableView;
        this.isOpen = isOpen;
        orderDelivered = tableView.isOrderDelivered();
        orderDeliveredTime = tableView.getOrderDeliveryTime();
    }

    public boolean isOpen() {
        return isOpen;
    }
}
