package com.inspirationlogical.receipt.service;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.model.view.ProductView;
import com.inspirationlogical.receipt.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.model.view.TableView;
import com.inspirationlogical.receipt.model.view.TableViewImpl;

import javax.persistence.EntityManager;
import java.util.Collection;

public class RetailServicesImpl extends AbstractServices implements RetailServices {

    @Inject
    public RetailServicesImpl(EntityManager manager) {
        super(manager);
    }

    @Override
    public void openTable(TableView tableView) {
        ((TableViewImpl)tableView).getAdapter().openTable();
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
