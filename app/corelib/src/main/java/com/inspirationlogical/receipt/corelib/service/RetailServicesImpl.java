package com.inspirationlogical.receipt.corelib.service;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.*;

import javax.persistence.EntityManager;
import java.util.Collection;

public class RetailServicesImpl extends AbstractServices implements RetailServices {

    @Inject
    public RetailServicesImpl(EntityManager manager) {
        super(manager);
    }

    @Override
    public void openTable(TableView tableView) {
        getTableAdapter(tableView).openTable();
    }

    @Override
    public void sellProduct(TableView tableView, ProductView productView, int amount, boolean takeAway) {
        getTableAdapter(tableView).getActiveReceipt().sellProduct(getProductAdapter(productView), amount, takeAway);
    }

    @Override
    public void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway) {
        getTableAdapter(tableView).getActiveReceipt().sellAdHocProduct(adHocProductParams, takeAway);
    }

    @Override
    public void payTable(TableView tableView, PaymentParams paymentParams) {
        getTableAdapter(tableView).payTable(paymentParams);
    }

    @Override
    public void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        getTableAdapter(tableView).paySelective(records, paymentParams);
    }

    @Override
    public ReceiptRecordView cloneReceiptRecordView(TableView tableView, ReceiptRecordView receiptRecordView, double amount) {
        //TODO: Write test!
        return new ReceiptRecordViewImpl(getTableAdapter(tableView).getActiveReceipt().cloneReceiptRecordAdapter(getReceiptRecordAdapter(receiptRecordView), amount));
    }
}
