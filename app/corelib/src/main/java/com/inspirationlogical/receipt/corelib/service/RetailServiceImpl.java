package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyConsumptionService;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService;
import com.inspirationlogical.receipt.corelib.service.receipt_record.ReceiptRecordService;
import com.inspirationlogical.receipt.corelib.service.table.TableServiceConfig;
import com.inspirationlogical.receipt.corelib.service.table.TableServicePay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class RetailServiceImpl extends AbstractService implements RetailService {

    final private static Logger logger = LoggerFactory.getLogger(RetailServiceImpl.class);

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private ReceiptRecordService receiptRecordService;

    @Autowired
    private TableServicePay tableServicePay;

    @Autowired
    private TableServiceConfig tableServiceConfig;

    @Autowired
    private DailyClosureService dailyClosureService;

    @Autowired
    private DailyConsumptionService dailyConsumptionService;

    @Autowired
    RetailServiceImpl(EntityViews entityViews) {
        super(entityViews);
    }

    @Override
    public void sellProduct(TableView tableView, ProductView productView, int quantity, boolean isTakeAway, boolean isGift) {
        receiptService.sellProduct(tableView, productView, quantity, isTakeAway, isGift);
        logger.info("A product was sold: quantity: " + quantity + ", takeAway: " + isTakeAway + " isGift: " + isGift + " " + productView + " ," + tableView);
    }

    @Override
    public void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean takeAway) {
        receiptService.sellAdHocProduct(tableView, adHocProductParams, takeAway);
        logger.info("An Ad Hoc product was sold: takeAway: " + takeAway + " " + adHocProductParams + " ," + tableView);
    }

    @Override
    public void sellGameFee(TableView tableView, int quantity) {
        logger.info("A game fee was sold: quantity: " + quantity + ", " + tableView);
        receiptService.sellGameFee(tableView, quantity);
    }

    @Override
    public ReceiptRecordView getLatestGameFee(TableView tableView) {
        return receiptService.getLatestGameFee(tableView);

    }

    @Override
    public TableView payTable(int tableNumber, PaymentParams paymentParams) {
        logger.info("A table was paid: " + tableNumber + ", " + paymentParams);
        return tableServicePay.payTable(tableNumber, paymentParams);
    }

    @Override
    public void paySelective(TableView tableView, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        logger.info("A table was selectively paid: " + tableView + ", " + paymentParams);
        tableServicePay.paySelective(tableView, records, paymentParams);
    }

    @Override
    public void payPartial(TableView tableView, double partialValue, PaymentParams paymentParams) {
        logger.info("A table was partially paid: partialValue:" + partialValue + ", " + tableView + ", " + paymentParams);
        tableServicePay.payPartial(tableView, partialValue, paymentParams);
    }

    @Override
    public ReceiptRecordView cloneReceiptRecordView(ReceiptRecordView receiptRecordView, double quantity) {
        logger.info("A receipt record was cloned: quantity:" + quantity + ", " + receiptRecordView);
        return receiptRecordService.cloneReceiptRecord(receiptRecordView, quantity);
    }

    @Override
    public void cancelReceiptRecord(ReceiptRecordView receiptRecordView) {
        receiptRecordService.cancelReceiptRecord(receiptRecordView);
        logger.info("A receipt record was canceled: " + receiptRecordView);
    }

    @Override
    public void mergeReceiptRecords(ReceiptView receiptView) {
        receiptService.mergeReceiptRecords(receiptView);
        logger.info("A the records of a receipt were merged: " + receiptView);
    }

    @Override
    public List<LocalDateTime> getClosureTimes(LocalDate startDate, LocalDate endDate) {
        return dailyClosureService.getClosureTimes(startDate, endDate);
    }

    public void increaseSoldQuantity(ReceiptRecordView receiptRecordView, double amount, boolean isSale) {
        receiptRecordService.increaseSoldQuantity(receiptRecordView, amount, isSale);
    }

    @Override
    public void decreaseSoldQuantity(ReceiptRecordView receiptRecordView, double amount) {
        receiptRecordService.decreaseSoldQuantity(receiptRecordView, amount);
    }

    @Override
    public RecentConsumption getRecentConsumption(TableView tableView) {
        return tableServiceConfig.getRecentConsumption(tableView);
    }
}
