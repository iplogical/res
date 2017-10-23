package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.restaurant.RestaurantAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

/**
 * Created by Bálint on 2017.03.13..
 */
public class RestaurantViewImpl extends AbstractModelViewImpl<RestaurantAdapter>
    implements RestaurantView {

    public RestaurantViewImpl(RestaurantAdapter adapter) {
        super(adapter);
    }

    @Override
    public String getRestaurantName() {
        return adapter.getAdaptee().getRestaurantName();
    }

    @Override
    public String getCompanyName() {
        return adapter.getAdaptee().getCompanyName();
    }

    @Override
    public int getConsumptionOfTheDay(PaymentMethod paymentMethod) {
        return adapter.getConsumptionOfTheDay(paymentMethod);
    }
}
