package com.inspirationlogical.receipt.waiter.viewstate;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

import lombok.Data;

import static com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState.DiscountType.ABSOLUTE;
import static com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState.DiscountType.PERCENT;
import static com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState.PaymentType.*;

/**
 * Created by Bálint on 2017.03.28..
 */
public @Data class PaymentViewState {
    private PaymentMethod paymentMethod;

    private PaymentType paymentType;

    private DiscountType discountType;

    private boolean automaticGameFee;

    public boolean isFullPayment() {
        return paymentType == FULL;
    }

    public boolean isSelectivePayment() {
        return paymentType == SELECTIVE;
    }

    public boolean isSinglePayment() {
        return paymentType == SINGLE;
    }

    public boolean isPartialPayment() {
        return paymentType == PARTIAL;
    }

    public boolean isDiscountAbsolute() {
        return discountType == ABSOLUTE;
    }

    public boolean isDiscountPercent() {
        return discountType == PERCENT;
    }

    public enum PaymentType {
        FULL,
        SELECTIVE,
        SINGLE,
        PARTIAL
    }

    public enum DiscountType {
        NONE,
        ABSOLUTE,
        PERCENT
    }
}
