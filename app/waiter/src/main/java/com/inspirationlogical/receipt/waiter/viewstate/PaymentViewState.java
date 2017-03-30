package com.inspirationlogical.receipt.waiter.viewstate;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import lombok.Data;

/**
 * Created by Bálint on 2017.03.28..
 */
public @Data class PaymentViewState {
    private PaymentMethod paymentMethod;

    private boolean selectivePayment;

    private boolean singlePayment;

    private boolean partialPayment;

    private boolean automaticGameFee;

    private boolean discountAbsolute;

    private boolean discountPercent;

    public boolean isFullPayment() {
        return !(selectivePayment || singlePayment || partialPayment);
    }
}
