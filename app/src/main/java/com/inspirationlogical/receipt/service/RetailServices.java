package com.inspirationlogical.receipt.service;

import com.inspirationlogical.receipt.model.view.ProductView;
import com.inspirationlogical.receipt.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.model.view.TableView;

import java.util.Collection;

public interface RetailServices {

    void openTable(TableView tableView);

    void sellProduct(TableView tableView, ProductView productView, int amount, PaymentParams paymentParams);

    void sellAdHocProduct(TableView tableView, ProductView productView, int amount, PaymentParams paymentParams);

    void payTable(TableView tableView, PaymentParams paymentParams);

    void paySelective(TableView tableView, Collection<Pair<ReceiptRecordView, Double>> records, PaymentParams paymentParams);
}
