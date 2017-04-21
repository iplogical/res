package com.inspirationlogical.receipt.corelib.service;

import java.util.Collection;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;

public class RetailServiceImpl extends AbstractService implements RetailService {

    @Inject
    public RetailServiceImpl(EntityManager manager) {
        super(manager);
    }

    @Override
    public void openTable(TableView tableView) {
        getTableAdapter(tableView).openTable();
    }

    @Override
    public void sellProduct(TableView tableView, ProductView productView, int amount, boolean isTakeAway, boolean isGift) {
        getTableAdapter(tableView).getActiveReceipt().sellProduct(getProductAdapter(productView), amount, isTakeAway, isGift);
    }

    @Override
    public void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway) {
        getTableAdapter(tableView).getActiveReceipt().sellAdHocProduct(adHocProductParams, takeAway);
    }

    @Override
    public void sellGameFee(TableView tableView, int quantity) {
        getTableAdapter(tableView).getActiveReceipt().sellGameFee(quantity);
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

    @Override
    public void cancelReceiptRecord(TableView tableView, ReceiptRecordView receiptRecordView) {
        getTableAdapter(tableView).getActiveReceipt().cancelReceiptRecord(getReceiptRecordAdapter(receiptRecordView));
    }

    @Override
    public void mergeReceiptRecords(ReceiptView receiptView) {
        getReceiptAdapter(receiptView).mergeReceiptRecords();
    }

    @Override
    public void printDailyConsumption() {

    }
}
