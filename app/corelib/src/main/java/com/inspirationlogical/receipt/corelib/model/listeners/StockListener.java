package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptServicePay;
import com.inspirationlogical.receipt.corelib.service.stock.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bálint on 2017.04.04..
 */
@Component
public class StockListener {

    private static final Logger logger = LoggerFactory.getLogger(StockListener.class);

    @Autowired
    private StockService stockService;

    @FunctionalInterface
    public interface StockUpdateListener {
        void finished();
    }

    private static List<StockUpdateListener> observerList = new ArrayList<>();

    public static void addObserver(StockUpdateListener o) {
        observerList.add(o);
    }

    public void onClose(Receipt receipt) {
        BackgroundThread.execute(() -> {
            logger.info("Stock listener startTime: " + LocalDateTime.now());
            logger.info("Updating stock record for receipt: " + receipt.toString());
            receipt.getRecords().forEach(receiptRecord -> {
                    logger.info("Updating stock record for receiptRecord: " + receiptRecord.toString());
                stockService.increaseStock(receiptRecord, receipt.getType());
            });
            observerList.forEach(StockUpdateListener::finished);
            observerList.clear();
            logger.info("StockListener executed successfully");
            logger.info("Stock listener endTime: " + LocalDateTime.now());
        });
    }
}
