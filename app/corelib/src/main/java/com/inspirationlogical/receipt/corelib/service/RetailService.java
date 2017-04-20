package com.inspirationlogical.receipt.corelib.service;

import java.util.Collection;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;

public interface RetailService {

    void openTable(TableView tableView);

    void sellProduct(TableView tableView, ProductView productView, int amount, boolean isTakeAway, boolean isGift);

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean isTakeAway);

    void sellGameFee(TableView tableView, int quantity);

    void payTable(TableView tableView, PaymentParams paymentParams);

    void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    ReceiptRecordView cloneReceiptRecordView(TableView tableView, ReceiptRecordView receiptRecordView, double amount);

    void cancelReceiptRecord(TableView tableView, ReceiptRecordView receiptRecordView);

    void mergeReceiptRecords(ReceiptView receiptView);
}
