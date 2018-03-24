package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

/**
 * Created by Bálint on 2017.03.13..
 */
public interface RestaurantView {

    long getRestaurantId();

    String getRestaurantName();

    String getCompanyName();

    int getConsumptionOfTheDay(PaymentMethod paymentMethod);

}
