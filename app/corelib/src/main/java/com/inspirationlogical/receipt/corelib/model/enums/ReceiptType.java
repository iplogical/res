package com.inspirationlogical.receipt.corelib.model.enums;

public enum ReceiptType {

    SALE,
    PURCHASE,
    INVENTORY,
    DISPOSAL,
    OTHER;

    public static boolean isStockDecrement(ReceiptType type) {
        return type.equals(SALE) || type.equals(DISPOSAL);
    }

    public static boolean isStockIncrement(ReceiptType type) {
        return type.equals(PURCHASE);
    }

    public static boolean isInventory(ReceiptType type) {
        return type.equals(INVENTORY);
    }
}
