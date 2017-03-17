package com.inspirationlogical.receipt.corelib.model.enums;

import lombok.NonNull;

public enum ReceiptRecordType {

    HERE,
    TAKE_AWAY;

    public static VATName getVatName(ReceiptRecordType type) {
        if(type.equals(HERE)) return VATName.REDUCED;
        else if(type.equals(TAKE_AWAY)) return VATName.NORMAL;
        else return null;
    }
}
