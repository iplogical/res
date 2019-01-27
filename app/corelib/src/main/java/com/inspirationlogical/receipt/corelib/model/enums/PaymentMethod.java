package com.inspirationlogical.receipt.corelib.model.enums;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

public enum PaymentMethod {

    CASH,
    CREDIT_CARD,
    COUPON;

    public String toI18nString() {
        if(this.equals(CASH))
            return Resources.CONFIG.getString("PaymentMethod.Cash");
        if(this.equals(CREDIT_CARD))
            return Resources.CONFIG.getString("PaymentMethod.CreditCard");
        if(this.equals(COUPON))
            return Resources.CONFIG.getString("PaymentMethod.Coupon");
        return "";
    }

    public String toPrintString() {
        if(this.equals(CASH))
            return Resources.CONFIG.getString("PaymentMethod.Cash");
        if(this.equals(CREDIT_CARD))
            return Resources.CONFIG.getString("PaymentMethod.CreditCard");
        if(this.equals(COUPON))
            return Resources.CONFIG.getString("PaymentMethod.Cash");
        return "";
    }
}
