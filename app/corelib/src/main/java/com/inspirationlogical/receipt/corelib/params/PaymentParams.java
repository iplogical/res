package com.inspirationlogical.receipt.corelib.params;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

import lombok.Builder;
import lombok.Data;

@Builder
public @Data class PaymentParams {

    private PaymentMethod paymentMethod;

    private int userCode;

    private int discountAbsolute;

    private double discountPercent;

}
