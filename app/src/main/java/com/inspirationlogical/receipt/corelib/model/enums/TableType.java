package com.inspirationlogical.receipt.corelib.model.enums;

import java.util.*;

import static java.util.Arrays.asList;

public enum TableType {

    NORMAL,
    VIRTUAL,
    PURCHASE,
    INVENTORY,
    DISPOSAL,
    OTHER;

    public static boolean isSpecial(TableType type) {
        return type.equals(PURCHASE) || type.equals(INVENTORY) || type.equals(DISPOSAL) || type.equals(OTHER);
    }

    public static List<Map.Entry<TableType, Long>> specialTypes() {
        List<Map.Entry<TableType, Long>> list = Arrays.asList(
                new AbstractMap.SimpleEntry<TableType, Long>(PURCHASE, 1L),
                new AbstractMap.SimpleEntry<TableType, Long>(INVENTORY, 1L),
                new AbstractMap.SimpleEntry<TableType, Long>(DISPOSAL, 1L),
                new AbstractMap.SimpleEntry<TableType, Long>(OTHER, 1L));
        list.sort(getComparator());
        return list;
    }

    public static Comparator<Map.Entry<TableType, Long>> getComparator() {
        return (lhs, rhs) -> {return lhs.toString().compareTo(rhs.toString());};
    }
}
