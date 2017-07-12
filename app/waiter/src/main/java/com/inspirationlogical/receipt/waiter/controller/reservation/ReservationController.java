package com.inspirationlogical.receipt.waiter.controller.reservation;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
public interface ReservationController extends Controller {
    public void setRestaurantView(RestaurantView restaurantView);
}
