package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.PriceModifierAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

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
        return null;
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
        return adapter.getAdaptee().getType().toString();
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
    public String getStartTime() {
        return adapter.getAdaptee().getStartTime().toString();
    }

    @Override
    public String getEndTime() {
        return adapter.getAdaptee().getEndTime().toString();
    }

    @Override
    public String getRepeatPeriod() {
        return adapter.getAdaptee().getPeriod().toString();
    }

    @Override
    public String getPeriodMultiplier() {
        return String.valueOf(adapter.getAdaptee().getPeriodMultiplier());
    }
}
