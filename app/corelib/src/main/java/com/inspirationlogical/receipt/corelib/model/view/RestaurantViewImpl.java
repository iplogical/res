package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import lombok.Getter;

import java.util.Map;

/**
 * Created by Bálint on 2017.03.13..
 */
@Getter
public class RestaurantViewImpl implements RestaurantView {

    private long restaurantId;
    private String restaurantName;
    private String companyName;
    private Map<PaymentMethod, Integer> consumptionOfTheDay;

    public RestaurantViewImpl(Restaurant restaurant, Map<PaymentMethod, Integer> consumptionOfTheDay) {
        restaurantId = restaurant.getId();
        restaurantName = restaurant.getRestaurantName();;
        companyName = restaurant.getCompanyName();
        this.consumptionOfTheDay = consumptionOfTheDay;
    }

    @Override
    public int getConsumptionOfTheDay(PaymentMethod paymentMethod) {
        return consumptionOfTheDay.get(paymentMethod);
    }
}
