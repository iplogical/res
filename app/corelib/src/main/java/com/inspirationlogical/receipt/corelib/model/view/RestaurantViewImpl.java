package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.adapter.RestaurantAdapter;

import java.util.function.Predicate;

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
    public int getConsumptionOfTheDay(Predicate<Receipt> filter) {
        return adapter.getConsumptionOfTheDay(filter);
    }
}
