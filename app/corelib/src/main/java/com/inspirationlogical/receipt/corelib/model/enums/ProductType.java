package com.inspirationlogical.receipt.corelib.model.enums;

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
}
