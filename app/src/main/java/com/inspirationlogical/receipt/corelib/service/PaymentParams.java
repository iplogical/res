package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import lombok.Builder;
import lombok.Data;

@Builder
public @Data class PaymentParams {

    private ReceiptRecordType receiptRecordType;

    int discountAbsolute;

    Double discountPercent;

}
