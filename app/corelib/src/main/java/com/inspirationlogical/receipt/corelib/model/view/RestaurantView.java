package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;

import java.util.function.Predicate;

/**
 * Created by Bálint on 2017.03.13..
 */
public interface RestaurantView {
    String getRestaurantName();

    String getCompanyName();

    int getConsumptionOfTheDay(Predicate<Receipt> filter);

}
