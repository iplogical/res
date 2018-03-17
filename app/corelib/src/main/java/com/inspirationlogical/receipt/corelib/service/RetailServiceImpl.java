package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.restaurant.DailyConsumptionAdapter;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class RetailServiceImpl extends AbstractService implements RetailService {

    final private static Logger logger = LoggerFactory.getLogger(RetailServiceImpl.class);

    @Autowired
    RetailServiceImpl(EntityViews entityViews) {
        super(entityViews);
    }

    @Override
    public void openTable(TableView tableView) {
        getTableAdapter(tableView).openTable();
        logger.info("A table was opened: " + tableView);
    }

    @Override
    public boolean reOpenTable(TableView tableView) {
        if(getTableAdapter(tableView).reOpenTable()) {
            logger.info("A table was re-opened: " + tableView);
            return true;
        }
        return false;
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
    public List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate) {
        return DailyClosureAdapter.getClosureTimes(startDate, endDate);
    }

    @Override
    public void printAggregateConsumption(RestaurantView restaurantView, LocalDate startDate, LocalDate endDate) {
        new DailyConsumptionAdapter().printAggregatedConsumption(startDate, endDate);
        logger.info("The aggregated consumption was printed between: " + startDate + " - " + endDate + ", for restaurant" + restaurantView);
    }

    @Override
    public ReceiptView getAggregatedReceipt(RestaurantView restaurantView, LocalDate startDate, LocalDate endDate) {
        return new DailyConsumptionAdapter().getAggregatedReceipt(startDate, endDate);
    }

    @Override
    public void setOrderDelivered(TableView tableView, boolean delivered) {
        getTableAdapter(tableView).getOpenReceipt().setOrderDelivered(delivered);
    }

    @Override
    public void setOrderDeliveredTime(TableView tableView, LocalDateTime now) {
        getTableAdapter(tableView).getOpenReceipt().setOrderDeliveredTime(now);
    }
}
