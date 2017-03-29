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
    public void sellAdHocProduct(TableView tableView, int amount, AdHocProductParams adHocProductParams, PaymentParams paymentParams) {
        getTableAdapter(tableView).getActiveReceipt().sellAdHocProduct(amount, adHocProductParams, paymentParams);
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
    public ReceiptRecordView getReceiptRecordView(TableView tableView, ReceiptRecordView receiptRecordView) {
        //TODO: Write test!
        return new ReceiptRecordViewImpl(getTableAdapter(tableView).getActiveReceipt().getReceiptRecordAdapter(getReceiptRecordAdapter(receiptRecordView)));
    }
}
