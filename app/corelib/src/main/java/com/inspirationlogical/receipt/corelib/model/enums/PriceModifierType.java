package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

public enum PriceModifierType {

    // TODO: Implement this later.
//    FUTURE_PRICE_MODIFICATION,
    SIMPLE_DISCOUNT,
    QUANTITY_DISCOUNT;

    public String toI18nString() {
        if(this.equals(SIMPLE_DISCOUNT))
            return Resources.CONFIG.getString("PriceModifierType.SimpleDiscount");
        if(this.equals(QUANTITY_DISCOUNT))
            return Resources.CONFIG.getString("PriceModifierType.QuantityDiscount");
        return "";
    }
}
