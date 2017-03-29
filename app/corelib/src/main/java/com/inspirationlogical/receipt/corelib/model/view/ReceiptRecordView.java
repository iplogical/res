package com.inspirationlogical.receipt.corelib.model.view;

/**
 * Created by Bálint on 2017.03.15..
 */
public interface ReceiptRecordView extends AbstractView {
    Long getId();

    String getName();

    double getSoldQuantity();

    int getSalePrice();

    int getTotalPrice();

    double getDiscountPercent();

    double getVat();

    double getPaidQuantity();

    void setPaidQuantity(double quantity);

    void increaseSoldQuantity();

    void decreaseSoldQuantity();
}
