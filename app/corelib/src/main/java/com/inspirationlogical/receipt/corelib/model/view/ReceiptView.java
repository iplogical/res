package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;

import java.time.LocalDateTime;
import java.util.List;

public interface ReceiptView {

    long getId();

    List<ReceiptRecordView> getSoldProducts();

    long getTotalPrice();

    ReceiptType getType();

    ReceiptStatus getStatus();

    PaymentMethod getPaymentMethod();

    LocalDateTime getOpenTime();

    LocalDateTime getClosureTime();

    int getUserCode();

    int getSumPurchaseNetPrice();

    int getSumPurchaseGrossPrice();

    int getSumSaleNetPrice();

    int getSumSaleGrossPrice();

    double getDiscountPercent();

    Client getClient();
}