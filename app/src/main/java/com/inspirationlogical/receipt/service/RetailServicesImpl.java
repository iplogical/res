package com.inspirationlogical.receipt.service;

import com.inspirationlogical.receipt.model.view.ProductView;
import com.inspirationlogical.receipt.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.view.TableView;

import java.util.Collection;

public class RetailServicesImpl implements RetailServices {

    @Override
    public void openTable(TableView tableView) {

    }

    @Override
    public void sellProduct(TableView tableView, ProductView productView, int amount, PaymentParams paymentParams) {

    }

    @Override
    public void sellAdHocProduct(TableView tableView, ProductView productView, int amount, PaymentParams paymentParams) {

    }

    @Override
    public void payTable(TableView tableView, PaymentParams paymentParams) {

    }

    @Override
    public void paySelective(TableView tableView, Collection<Pair<ReceiptRecordView, Double>> records, PaymentParams paymentParams) {

    }
}
