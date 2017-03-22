package com.inspirationlogical.receipt.waiter.viewstate;

import lombok.Data;

/**
 * Created by Bálint on 2017.03.22..
 */
public @Data class TableViewState implements ViewState {

    private RestaurantViewState restaurantViewState;

    private boolean isOpen;
}
