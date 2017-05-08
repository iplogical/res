package com.inspirationlogical.receipt.corelib.model.view;

import java.time.LocalDateTime;
import java.util.List;

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

    List<LocalDateTime> getCreated();

    void increaseSoldQuantity(double amount, boolean isSale);

    void decreaseSoldQuantity(double amount);

    boolean isPartiallyPayable();
}
