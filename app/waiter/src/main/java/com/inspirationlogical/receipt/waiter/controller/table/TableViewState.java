package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantViewState;
import lombok.Data;

@Data
public class TableViewState implements ViewState {

    private RestaurantViewState restaurantViewState;

    private boolean selected;

    TableViewState(RestaurantViewState restaurantViewState) {
        this.restaurantViewState = restaurantViewState;
    }
}
