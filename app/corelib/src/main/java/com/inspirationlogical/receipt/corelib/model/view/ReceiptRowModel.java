package com.inspirationlogical.receipt.corelib.model.view;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ReceiptRowModel {

    private String receiptId;
    private String receiptTotalPrice;
    private String receiptPaymentMethod;
    private String receiptOpenTime;
    private String receiptClosureTime;
}