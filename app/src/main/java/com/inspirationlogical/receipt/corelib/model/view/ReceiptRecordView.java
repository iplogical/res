package com.inspirationlogical.receipt.corelib.model.view;

/**
 * Created by Bálint on 2017.03.15..
 */
public interface ReceiptRecordView {
    Long getId();

    double getPaidQuantity();

    void setPaidQuantity(double quantity);
}
