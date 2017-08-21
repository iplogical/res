package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

public enum QuantityUnit {

    LITER,
    CENTILITER,
    KILOGRAM,
    GRAM;

    public String toI18nString() {
        if(this.equals(LITER))
            return Resources.CONFIG.getString("QuantityUnit.Liter");
        if(this.equals(CENTILITER))
            return Resources.CONFIG.getString("QuantityUnit.Centiliter");
        if(this.equals(KILOGRAM))
            return Resources.CONFIG.getString("QuantityUnit.Kilogram");
        if(this.equals(GRAM))
            return Resources.CONFIG.getString("QuantityUnit.Gram");
        return "";
    }

}
