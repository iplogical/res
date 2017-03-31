package com.inspirationlogical.receipt.corelib.service;

import java.util.Collection;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

public interface RetailServices {

    void openTable(TableView tableView);

    void sellProduct(TableView tableView, ProductView productView, int amount, boolean takeAway);

    void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway);

    void sellGameFee(TableView tableView, int quantity);

    void payTable(TableView tableView, PaymentParams paymentParams);

    void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams);

    ReceiptRecordView cloneReceiptRecordView(TableView tableView, ReceiptRecordView receiptRecordView, double amount);
}
