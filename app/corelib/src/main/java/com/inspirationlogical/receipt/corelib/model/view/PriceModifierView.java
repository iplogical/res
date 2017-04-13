package com.inspirationlogical.receipt.corelib.model.view;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
public interface PriceModifierView extends AbstractView {
    String getOwnerName();

    String getType();

    String getQuantityLimit();

    String getDiscountPercent();

    String getStartDate();

    String getEndDate();

    String getRepeatPeriod();

    String getPeriodMultiplier();

    String getStartTime();

    String getEndTime();

    String getWeekOfDay();
}
