package com.inspirationlogical.receipt.corelib.service;

import java.util.Collection;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetailServiceImpl extends AbstractService implements RetailService {

    final private static Logger logger = LoggerFactory.getLogger(RetailServiceImpl.class);

    @Inject
    public RetailServiceImpl(EntityManager manager) {
        super(manager);
    }

    @Override
    public void openTable(TableView tableView) {
        getTableAdapter(tableView).openTable();
        logger.info("A table was opened: " + tableView);
    }

    @Override
    public void sellProduct(TableView tableView, ProductView productView, int quantity, boolean isTakeAway, boolean isGift) {
        getTableAdapter(tableView).getOpenReceipt().sellProduct(getProductAdapter(productView), quantity, isTakeAway, isGift);
        logger.info("A product was sold: quantity: " + quantity + ", takeAway: " + isTakeAway + " isGift: " + isGift + " " + productView + " ," + tableView);
    }

    @Override
    public void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway) {
        getTableAdapter(tableView).getOpenReceipt().sellAdHocProduct(adHocProductParams, takeAway);
        logger.info("An Ad Hoc product was sold: takeAway: " + takeAway + " " + adHocProductParams + " ," + tableView);
    }

    @Override
    public ReceiptRecordView sellGameFee(TableView tableView, int quantity) {
        logger.info("A game fee was sold: quantity: " + quantity + ", " + tableView);
        return new ReceiptRecordViewImpl(getTableAdapter(tableView).getOpenReceipt().sellGameFee(quantity));
    }

    @Override
    public void payTable(TableView tableView, PaymentParams paymentParams) {
        logger.info("A table was paid: "  + tableView + ", " + paymentParams);
        getTableAdapter(tableView).payTable(paymentParams);
    }

    @Override
    public void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        logger.info("A table was selectively paid: "  + tableView + ", " + paymentParams);
        getTableAdapter(tableView).paySelective(records, paymentParams);
    }

    @Override
    public void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams) {
        logger.info("A table was partially paid: partialValue:" + partialValue + ", " + tableView + ", " + paymentParams);
        getTableAdapter(tableView).payPartial(partialValue, paymentParams);
    }

    @Override
    public ReceiptRecordView cloneReceiptRecordView(TableView tableView, ReceiptRecordView receiptRecordView, double quantity) {
        logger.info("A receipt record was cloned: quantity:" + quantity + ", " + receiptRecordView + ", " + tableView);
        return new ReceiptRecordViewImpl(getTableAdapter(tableView).getOpenReceipt().cloneReceiptRecordAdapter(getReceiptRecordAdapter(receiptRecordView), quantity));
    }

    @Override
    public void cancelReceiptRecord(TableView tableView, ReceiptRecordView receiptRecordView) {
        getTableAdapter(tableView).getOpenReceipt().cancelReceiptRecord(getReceiptRecordAdapter(receiptRecordView));
        logger.info("A receipt record was canceled: " + receiptRecordView + ", " + tableView);
    }

    @Override
    public void mergeReceiptRecords(ReceiptView receiptView) {
        getReceiptAdapter(receiptView).mergeReceiptRecords();
        logger.info("A the records of a receipt were merged: " + receiptView);
    }

    @Override
    public void printDailyConsumption(RestaurantView restaurantView) {
        getRestaurantAdapter(restaurantView).printDailyConsumption();
        logger.info("The daily consumption was printed: " + restaurantView);
    }
}
