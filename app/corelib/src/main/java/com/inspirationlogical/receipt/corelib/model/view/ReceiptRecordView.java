package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Bálint on 2017.03.15..
 */
public interface ReceiptRecordView extends AbstractView {
    long getId();

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

    boolean isPartiallyPayable();

    ReceiptStatus getOwnerStatus();

}
