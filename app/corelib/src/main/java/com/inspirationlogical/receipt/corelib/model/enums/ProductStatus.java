package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

public enum ProductStatus {
    ACTIVE,
    DELETED;

    public String toI18nString() {
        if(this.equals(ACTIVE))
            return Resources.CONFIG.getString("ProductStatus.Active");
        if(this.equals(DELETED))
            return Resources.CONFIG.getString("ProductStatus.Deleted");
        return "";
    }

    }
