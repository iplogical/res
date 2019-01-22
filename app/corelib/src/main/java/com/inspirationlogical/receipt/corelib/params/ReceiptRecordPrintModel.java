package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ReceiptRecordPrintModel {

    private String productName;
    private double soldQuantity;
    private int productPrice;
    private int totalPrice;
    private boolean discount;
}
