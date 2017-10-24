package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

public enum PriceModifierRepeatPeriod {

    DAILY,
    WEEKLY,
    NO_REPETITION;

    public String toI18nString() {
        if(this.equals(DAILY))
            return Resources.CONFIG.getString("PriceModifierRepeatPeriod.Daily");
        if(this.equals(WEEKLY))
            return Resources.CONFIG.getString("PriceModifierRepeatPeriod.Weekly");
        if(this.equals(NO_REPETITION))
            return Resources.CONFIG.getString("PriceModifierRepeatPeriod.NoRepetition");
        return "";
    }
}
