package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface RetailService {

    void openTable(TableView tableView);

    boolean reOpenTable(TableView tableView);

    void sellProduct(TableView tableView, ProductView productView, int amount, boolean isTakeAway, boolean isGift);

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean isTakeAway);

    ReceiptRecordView sellGameFee(TableView tableView, int quantity);

    void payTable(TableView tableView, PaymentParams paymentParams);

    void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams);

    ReceiptRecordView cloneReceiptRecordView(ReceiptRecordView receiptRecordView, double amount);

    void cancelReceiptRecord(ReceiptRecordView receiptRecordView);

    void mergeReceiptRecords(ReceiptView receiptView);

    List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate);

    void printAggregateConsumption(RestaurantView restaurantView, LocalDate startDate, LocalDate endDate);

    ReceiptView getAggregatedReceipt(RestaurantView restaurantView, LocalDate startDate, LocalDate endDate);

    void setOrderDelivered(TableView tableView, boolean delivered);

    void setOrderDeliveredTime(TableView tableView, LocalDateTime now);

    void increaseSoldQuantity(ReceiptRecordView receiptRecord, double amount, boolean isSale);

    void decreaseSoldQuantity(ReceiptRecordView receiptRecord, double amount);
}
