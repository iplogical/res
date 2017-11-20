package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import lombok.Data;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
public @Data
class PriceModifierViewModel {

    private String ownerName;
    private String name;
    private String type;
    private String quantityLimit;
    private String discountPercent;
    private String startDate;
    private String endDate;
    private String repeatPeriod;
    private String periodMultiplier;
    private String startTime;
    private String endTime;
    private String dayOfWeek;

    public PriceModifierViewModel(PriceModifierView priceModifierView) {
        ownerName = priceModifierView.getOwnerName() == null ? "" : priceModifierView.getOwnerName();
        name = priceModifierView.getName() == null ? "" : priceModifierView.getName();
        type = priceModifierView.getType() == null ? "" : priceModifierView.getType();
        quantityLimit = priceModifierView.getQuantityLimit() == null ? "" : priceModifierView.getQuantityLimit();
        discountPercent = priceModifierView.getDiscountPercent() == null ? "" : priceModifierView.getDiscountPercent();
        startDate = priceModifierView.getStartDate() == null ? "" : priceModifierView.getStartDate();
        endDate = priceModifierView.getEndDate() == null ? "" : priceModifierView.getEndDate();
        repeatPeriod = priceModifierView.getRepeatPeriod() == null ? "" : priceModifierView.getRepeatPeriod();
        periodMultiplier = priceModifierView.getPeriodMultiplier() == null ? "" : priceModifierView.getPeriodMultiplier();
        startTime = priceModifierView.getStartTime() == null ? "" : priceModifierView.getStartTime();
        endTime = priceModifierView.getEndTime() == null ? "" : priceModifierView.getEndTime();
        dayOfWeek = priceModifierView.getDayOfWeek() == null ? "" : priceModifierView.getDayOfWeek();
    }
}
