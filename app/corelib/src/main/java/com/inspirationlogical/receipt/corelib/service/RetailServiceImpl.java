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
        getTableAdapter(tableView).getOpenReceipt().sellProduct(getProductAdapter(productView), amount, isTakeAway, isGift);
    }

    @Override
    public void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway) {
        getTableAdapter(tableView).getOpenReceipt().sellAdHocProduct(adHocProductParams, takeAway);
    }

    @Override
    public ReceiptRecordView sellGameFee(TableView tableView, int quantity) {
        return new ReceiptRecordViewImpl(getTableAdapter(tableView).getOpenReceipt().sellGameFee(quantity));
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
        return new ReceiptRecordViewImpl(getTableAdapter(tableView).getOpenReceipt().cloneReceiptRecordAdapter(getReceiptRecordAdapter(receiptRecordView), amount));
    }

    @Override
    public void cancelReceiptRecord(TableView tableView, ReceiptRecordView receiptRecordView) {
        getTableAdapter(tableView).getOpenReceipt().cancelReceiptRecord(getReceiptRecordAdapter(receiptRecordView));
    }

    @Override
    public void mergeReceiptRecords(ReceiptView receiptView) {
        getReceiptAdapter(receiptView).mergeReceiptRecords();
    }

    @Override
    public void printDailyConsumption(RestaurantView restaurantView) {
        getRestaurantAdapter(restaurantView).printDailyConsumption();
    }
}
