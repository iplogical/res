package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

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

    public String toI18nString() {
        if(this.equals(AGGREGATE))
            return Resources.CONFIG.getString("ProductCategoryType.Aggregate");
        if(this.equals(LEAF))
            return Resources.CONFIG.getString("ProductCategoryType.Leaf");
        return "";
    }
}
