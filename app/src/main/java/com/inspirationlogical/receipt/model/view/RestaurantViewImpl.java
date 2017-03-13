package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.RestaurantAdapter;
import com.inspirationlogical.receipt.model.view.RestaurantView;

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
}
