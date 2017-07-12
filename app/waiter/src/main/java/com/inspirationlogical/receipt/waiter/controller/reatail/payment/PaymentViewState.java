package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

import lombok.Data;

import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.DiscountType.ABSOLUTE;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.DiscountType.PERCENT;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.PaymentType.*;

/**
 * Created by Bálint on 2017.03.28..
 */
public @Data class PaymentViewState {
    private PaymentMethod paymentMethod;

    private PaymentType paymentType;

    private DiscountType discountType;

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
