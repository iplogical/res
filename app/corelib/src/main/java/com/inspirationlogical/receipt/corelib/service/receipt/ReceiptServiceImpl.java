package com.inspirationlogical.receipt.corelib.service.receipt;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class ReceiptServiceImpl implements ReceiptService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptServiceImpl.class);

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private ReceiptServiceMerge receiptServiceMerge;

    @Autowired
    private ReceiptServicePay receiptServicePay;

    @Autowired
    private ReceiptServiceSell receiptServiceSell;

    @Autowired
    private ReceiptServiceStock receiptServiceStock;

    @Override
    public ReceiptView getOpenReceipt(int tableNumber) {
        Receipt receipt = receiptRepository.getOpenReceipt(tableNumber);
        if (receipt == null) {
            return null;
        }
        return new ReceiptView(receipt);
    }

    @Override
    public void sellProduct(TableView tableView, ProductView productView, boolean isTakeAway, boolean isGift) {
        receiptServiceSell.sellProduct(tableView, productView, isTakeAway, isGift);
        logger.info("A product was sold: takeAway: " + isTakeAway + " isGift: " + isGift + " " + productView + " ," + tableView);
    }

    @Override
    public void sellAdHocProduct(TableView tableView, AdHocProductParams adHocProductParams, boolean isTakeAway) {
        receiptServiceSell.sellAdHocProduct(tableView, adHocProductParams, isTakeAway);
        logger.info("An Ad Hoc product was sold: takeAway: " + isTakeAway + " " + adHocProductParams + " ," + tableView);
    }

    @Override
    public void sellGameFee(TableView tableView, int quantity) {
        receiptServiceSell.sellGameFee(tableView, quantity);
        logger.info("A game fee was sold: quantity: " + quantity + ", " + tableView);
    }

    @Override
    public ReceiptRecordView getLatestGameFee(TableView tableView) {
        return receiptServiceSell.getLatestGameFee(tableView);
    }

    @Override
    public void updateStock(List<StockParams> paramsList, ReceiptType receiptType, StockListener.StockUpdateListener listener) {
        receiptServiceStock.updateStock(paramsList, receiptType, listener);
    }

    @Override
    public void close(Receipt receipt, PaymentParams paymentParams) {
        receiptServicePay.close(receipt, paymentParams);
    }


    @Override
    public void paySelective(Receipt receipt, Collection<ReceiptRecordView> records, PaymentParams paymentParams) {
        receiptServicePay.paySelective(receipt, records, paymentParams);
    }

    @Override
    public void payPartial(Receipt receipt, double partialValue, PaymentParams paymentParams) {
        receiptServicePay.payPartial(receipt, partialValue, paymentParams);
    }

    @Override
    public void printReceiptFromSale(int number) {
        receiptServicePay.printReceiptFromSale(number);
    }

    @Override
    public void printAggregatedReceipt(DailyConsumptionModel dailyConsumptionModel) {
        receiptServicePay.printAggregatedReceipt(dailyConsumptionModel);
    }

    @Override
    public void setSumValues(ReceiptView receiptView) {
        receiptServicePay.setSumValues(receiptView);
    }

    @Override
    public void mergeReceiptRecords(ReceiptView receiptView) {
        receiptServiceMerge.mergeReceiptRecords(receiptView);
        logger.info("A the records of a receipt were merged: " + receiptView);

    };

    @Override
    public List<ReceiptView> getReceipts(LocalDate startDate, LocalDate endDate) {
        return receiptRepository.getReceiptsByClosureTime(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay())
                .stream().map(ReceiptView::new).collect(toList());
    }
}
