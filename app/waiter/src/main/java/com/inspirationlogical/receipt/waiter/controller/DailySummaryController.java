package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;

/**
 * Created by TheDagi on 2017. 04. 21..
 */
public interface DailySummaryController  extends AbstractRetailController {

    void setRestaurantView(RestaurantView restaurantView);

    void setOpenConsumption(String openConsumption);

    void updatePriceFields();
}
