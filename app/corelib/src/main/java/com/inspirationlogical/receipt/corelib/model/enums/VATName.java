package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

public enum VATName {

    NORMAL,
    REDUCED,
    GREATLY_REDUCED,
    TAX_TICKET,
    TAX_FREE;

    public String toI18nString() {
        if(this.equals(NORMAL))
            return Resources.CONFIG.getString("Vat.Normal");
        if(this.equals(GREATLY_REDUCED))
            return Resources.CONFIG.getString("Vat.GreatlyReduced");
        return "";
    }
}
