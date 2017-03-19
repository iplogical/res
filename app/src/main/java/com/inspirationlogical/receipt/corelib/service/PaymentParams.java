package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import lombok.Builder;
import lombok.Data;

@Builder
public @Data class PaymentParams {

    private PaymentMethod paymentMethod;

    private ReceiptRecordType receiptRecordType;

    private int userCode;

    private int discountAbsolute;

    private double discountPercent;

}
