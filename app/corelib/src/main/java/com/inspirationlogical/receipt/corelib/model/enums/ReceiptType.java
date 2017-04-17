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
    public static boolean isSale(ReceiptType type) {
        return type.equals(SALE);
    }

    public static boolean isPurchase(ReceiptType type) {
        return type.equals(PURCHASE);
    }

    public static boolean isInventory(ReceiptType type) {
        return type.equals(INVENTORY);
    }

    public static boolean isDisposal(ReceiptType type) {
        return type.equals(DISPOSAL);
    }

    @Override
    public String toString() {
        return name;
    }
}