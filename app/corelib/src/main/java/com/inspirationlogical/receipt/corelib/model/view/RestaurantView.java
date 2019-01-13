package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import lombok.Getter;

import java.util.Map;

@Getter
public class RestaurantView {

    private long restaurantId;
    private String restaurantName;
    private String companyName;

    public RestaurantView(Restaurant restaurant) {
        restaurantId = restaurant.getId();
        restaurantName = restaurant.getRestaurantName();;
        companyName = restaurant.getCompanyName();
    }
}
