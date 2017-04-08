package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
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
    private String startTime;
    private String endTime;
    private String repeatPeriod;
    private String periodMultiplier;

    public PriceModifierViewModel(PriceModifierView priceModifierView) {
        ownerName = priceModifierView.getOwnerName();
        name = priceModifierView.getName();
        type = priceModifierView.getType();
        quantityLimit = priceModifierView.getQuantityLimit();
        discountPercent = priceModifierView.getDiscountPercent();
        startTime = priceModifierView.getStartTime();
        endTime = priceModifierView.getEndTime();
        repeatPeriod = priceModifierView.getRepeatPeriod();
        periodMultiplier = priceModifierView.getPeriodMultiplier();
    }
}
