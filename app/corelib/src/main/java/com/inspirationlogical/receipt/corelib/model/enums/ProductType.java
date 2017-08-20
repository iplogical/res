package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.utility.Resources;

public enum ProductType {

    SELLABLE,
    STORABLE,
    AD_HOC_PRODUCT,
    GAME_FEE_PRODUCT,
    PARTIALLY_PAYABLE;

    public static boolean needReceipt(ProductType type) {
        return type.equals(SELLABLE) || type.equals(PARTIALLY_PAYABLE) || type.equals(STORABLE);
    }

    public static boolean doNotNeedReceipt(ProductType type) {
        return !needReceipt(type);
    }

    public String toI18nString() {
        if(this.equals(SELLABLE))
            return Resources.CONFIG.getString("ProductType.Sellable");
        if(this.equals(PARTIALLY_PAYABLE))
            return Resources.CONFIG.getString("ProductType.PartiallyPayable");
        if(this.equals(STORABLE))
            return Resources.CONFIG.getString("ProductType.Storable");
        return "";
    }
}
