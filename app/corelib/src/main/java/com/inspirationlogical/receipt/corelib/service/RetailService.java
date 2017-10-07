package com.inspirationlogical.receipt.corelib.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;

public interface RetailService {

    void openTable(TableView tableView);

    void sellProduct(TableView tableView, ProductView productView, int amount, boolean isTakeAway, boolean isGift);

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean isTakeAway);

    ReceiptRecordView sellGameFee(TableView tableView, int quantity);

    void payTable(TableView tableView, PaymentParams paymentParams);

    void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams);

    ReceiptRecordView cloneReceiptRecordView(TableView tableView, ReceiptRecordView receiptRecordView, double amount);

    void cancelReceiptRecord(TableView tableView, ReceiptRecordView receiptRecordView);

    void mergeReceiptRecords(ReceiptView receiptView);

    List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate);

    void printAggregateConsumption(RestaurantView restaurantView, LocalDate startDate, LocalDate endDate);

    ReceiptView getAggregatedReceipt(RestaurantView restaurantView, LocalDate startDate, LocalDate endDate);
}
