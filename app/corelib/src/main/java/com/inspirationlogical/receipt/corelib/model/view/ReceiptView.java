package com.inspirationlogical.receipt.corelib.model.view;

import java.time.LocalDateTime;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;

public interface ReceiptView {

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

    VATSerie getVATSerie();

    Client getClient();
}