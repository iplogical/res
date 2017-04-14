package com.inspirationlogical.receipt.corelib.model.enums;

public enum ReceiptType {

    SALE("sale"),
    PURCHASE("purchase"),
    INVENTORY("inventory"),
    DISPOSAL("disposal"),
    OTHER("other");

    private String name;

    ReceiptType(String name) {
        this.name = name;
    }
    public static boolean isStockDecrement(ReceiptType type) {
        return type.equals(SALE) || type.equals(DISPOSAL);
    }

    public static boolean isStockIncrement(ReceiptType type) {
        return type.equals(PURCHASE);
    }

    public static boolean isInventory(ReceiptType type) {
        return type.equals(INVENTORY);
    }

    @Override
    public String toString() {
        return name;
    }
}