package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.PriceModifierAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
public class PriceModifierViewImpl extends AbstractModelViewImpl<PriceModifierAdapter>
        implements PriceModifierView {

    public PriceModifierViewImpl(PriceModifierAdapter adapter) {
        super(adapter);
    }

    @Override
    public String getName() {
        return adapter.getAdaptee().getName();
    }

    @Override
    public String getOwnerName() {
        if(adapter.getAdaptee().getOwner().getType().equals(ProductCategoryType.PSEUDO)) {
            return adapter.getAdaptee().getOwner().getProduct().getLongName();
        }
        return adapter.getAdaptee().getOwner().getName();
    }

    @Override
    public String getType() {
        return adapter.getAdaptee().getType().toI18nString();
    }

    @Override
    public String getQuantityLimit() {
        return String.valueOf(adapter.getAdaptee().getQuantityLimit());
    }

    @Override
    public String getDiscountPercent() {
        return String.valueOf(adapter.getAdaptee().getDiscountPercent());
    }

    @Override
    public String getStartDate() {
        return adapter.getAdaptee().getStartDate().toString();
    }

    @Override
    public String getEndDate() {
        return adapter.getAdaptee().getEndDate().toString();
    }

    @Override
    public String getRepeatPeriod() {
        return adapter.getAdaptee().getRepeatPeriod().toI18nString();
    }

    @Override
    public String getPeriodMultiplier() {
        return String.valueOf(adapter.getAdaptee().getRepeatPeriodMultiplier());
    }

    @Override
    public String getStartTime() {
        if(adapter.getAdaptee().getStartTime() == null) {
            return "";
        }
        return adapter.getAdaptee().getStartTime().toString();
    }

    @Override
    public String getEndTime() {
        if(adapter.getAdaptee().getEndTime() == null) {
            return "";
        }
        return adapter.getAdaptee().getEndTime().toString();
    }

    @Override
    public String getDayOfWeek() {
        if(adapter.getAdaptee().getDayOfWeek() == null) {
            return "";
        }
        return adapter.getAdaptee().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("hu"));
    }
}
