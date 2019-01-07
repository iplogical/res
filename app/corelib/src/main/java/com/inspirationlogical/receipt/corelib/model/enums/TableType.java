package com.inspirationlogical.receipt.corelib.model.enums;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public enum TableType {

    NORMAL,
    LOITERER,
    FREQUENTER,
    EMPLOYEE,
    PURCHASE,
    INVENTORY,
    DISPOSAL,
    ORPHANAGE,
    OTHER;

    public static boolean isSpecial(TableType type) {
        return type.equals(PURCHASE) || type.equals(INVENTORY) || type.equals(DISPOSAL) || type.equals(OTHER) || type.equals(ORPHANAGE);
    }

    public static boolean isDisplayable(TableType type) {
        return  type.equals(TableType.NORMAL)       ||
                type.equals(TableType.LOITERER)     ||
                type.equals(TableType.FREQUENTER)   ||
                type.equals(TableType.EMPLOYEE);
    }

    public static List<Map.Entry<TableType, Long>> specialTypes() {
        List<Map.Entry<TableType, Long>> list = Arrays.asList(
                new AbstractMap.SimpleEntry<TableType, Long>(PURCHASE, 1L),
                new AbstractMap.SimpleEntry<TableType, Long>(INVENTORY, 1L),
                new AbstractMap.SimpleEntry<TableType, Long>(DISPOSAL, 1L),
                new AbstractMap.SimpleEntry<TableType, Long>(ORPHANAGE, 1L),
                new AbstractMap.SimpleEntry<TableType, Long>(OTHER, 1L));
        list.sort(getComparator());
        return list;
    }

    public static Comparator<Map.Entry<TableType, Long>> getComparator() {
        return (lhs, rhs) -> {return lhs.toString().compareTo(rhs.toString());};
    }

    public static TableType getTableType(ReceiptType receiptType) {
        switch(receiptType) {
            case PURCHASE:
                return TableType.PURCHASE;
            case INVENTORY:
                return TableType.INVENTORY;
            case DISPOSAL:
                return TableType.DISPOSAL;
            default:
                return null;
        }
    }

    public static boolean isVirtualTable(TableType type) {
        return type.equals(LOITERER) || type.equals(FREQUENTER) || type.equals(EMPLOYEE);
    }
}
