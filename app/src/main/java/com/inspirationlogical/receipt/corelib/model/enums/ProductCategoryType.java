package com.inspirationlogical.receipt.corelib.model.enums;

public enum ProductCategoryType {

    ROOT,
    AGGREGATE,
    LEAF,
    PSEUDO,
    PSEUDO_DELETED;

    public static boolean isPseudo(ProductCategoryType type) {
        return type.equals(PSEUDO) || type.equals(PSEUDO_DELETED);
    }
}
