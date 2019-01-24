package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RetailService {

    TableView openTable(int tableNumber);

    TableView reOpenTable(int tableNumber);

    boolean isTableOpen(TableView tableView);

    void sellProduct(TableView tableView, ProductView productView, int amount, boolean isTakeAway, boolean isGift);

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean isTakeAway);

    ReceiptRecordView sellGameFee(TableView tableView, int quantity);

    TableView payTable(int tableNumber, PaymentParams paymentParams);

    void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams);

    ReceiptRecordView cloneReceiptRecordView(ReceiptRecordView receiptRecordView, double amount);

    void cancelReceiptRecord(ReceiptRecordView receiptRecordView);

    void mergeReceiptRecords(ReceiptView receiptView);

    List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate);

    void increaseSoldQuantity(ReceiptRecordView receiptRecord, double amount, boolean isSale);

    void decreaseSoldQuantity(ReceiptRecordView receiptRecord, double amount);

    RecentConsumption getRecentConsumption(TableView tableView);

    TableView setOrderDelivered(int tableNumber, boolean delivered);

    TableView setOrderDeliveredTime(int tableNumber, LocalDateTime deliveredTime);
}
