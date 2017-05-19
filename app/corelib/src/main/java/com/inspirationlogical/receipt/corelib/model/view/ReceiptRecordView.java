package com.inspirationlogical.receipt.corelib.model.view;

import java.time.LocalDateTime;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;

/**
 * Created by Bálint on 2017.03.15..
 */
public interface ReceiptRecordView extends AbstractView {
    Long getId();

    String getName();

    ReceiptRecordType getType();

    double getSoldQuantity();

    double getAbsoluteQuantity();

    int getPurchasePrice();

    int getSalePrice();

    int getTotalPrice();

    double getDiscountPercent();

    double getVat();

    List<LocalDateTime> getCreated();

    void increaseSoldQuantity(double amount, boolean isSale);

    void decreaseSoldQuantity(double amount);

    boolean isPartiallyPayable();
}
