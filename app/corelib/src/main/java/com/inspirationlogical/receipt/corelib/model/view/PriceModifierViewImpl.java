package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import lombok.Getter;
import lombok.ToString;

import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
@Getter
@ToString
public class PriceModifierViewImpl implements PriceModifierView {

    private long id;
    private String name;
    private String ownerName;
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
    
    public PriceModifierViewImpl(PriceModifier priceModifier) {
        id = priceModifier.getId();
        name = priceModifier.getName();
        ownerName = initOwnerName(priceModifier);
        type = priceModifier.getType().toI18nString();
        quantityLimit = String.valueOf(priceModifier.getQuantityLimit());
        discountPercent = String.valueOf(priceModifier.getDiscountPercent());
        startDate = priceModifier.getStartDate().toString();
        endDate = priceModifier.getEndDate().toString();
        repeatPeriod = priceModifier.getRepeatPeriod().toI18nString();
        periodMultiplier = String.valueOf(priceModifier.getRepeatPeriodMultiplier());
        startTime = initStartTime(priceModifier);
        endTime = initEndTime(priceModifier);
        dayOfWeek = initDayOfWeek(priceModifier);
    }

    private String initOwnerName(PriceModifier priceModifier) {
        if(priceModifier.getOwner().getType().equals(ProductCategoryType.PSEUDO)) {
            return priceModifier.getOwner().getProduct().getLongName();
        }
        return priceModifier.getOwner().getName();
    }

    private String initStartTime(PriceModifier priceModifier) {
        if(priceModifier.getStartTime() == null) {
            return "";
        }
        return priceModifier.getStartTime().toString();
    }

    private String initEndTime(PriceModifier priceModifier) {
        if(priceModifier.getEndTime() == null) {
            return "";
        }
        return priceModifier.getEndTime().toString();
    }

    private String initDayOfWeek(PriceModifier priceModifier) {
        if(priceModifier.getDayOfWeek() == null) {
            return "";
        }
        return priceModifier.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("hu"));
    }
}
