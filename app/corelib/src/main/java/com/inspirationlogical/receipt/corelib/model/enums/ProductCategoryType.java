package com.inspirationlogical.receipt.corelib.model.enums;

public enum ProductCategoryType {

    ROOT,
    AGGREGATE,
    LEAF,
    PSEUDO,
    PSEUDO_DELETED;

    public static boolean isRoot(ProductCategoryType type) {
        return type.equals(ROOT);
    }

    public static boolean isAggregate(ProductCategoryType type) {
        return type.equals(AGGREGATE);
    }

    public static boolean isLeaf(ProductCategoryType type) {
        return type.equals(LEAF);
    }

    public static boolean isPseudo(ProductCategoryType type) {
        return type.equals(PSEUDO) || type.equals(PSEUDO_DELETED);
    }
}
