package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Bálint on 2017.04.04..
 */
public class StockListener implements ReceiptAdapterPay.Listener {

    private static final Logger logger = LoggerFactory.getLogger(StockListener.class);

    @FunctionalInterface
    public interface StockUpdateListener {
        void finished();
    }
    private static List<StockUpdateListener> observerList = new ArrayList<>();

    @Override
    public void onOpen(ReceiptAdapterPay receipt) {

    }

    @Override
    public void onClose(Receipt receipt) {
        BackgroundThread.execute(() -> {
            logger.info("Updating stock record for receipt: " + receipt.toString());
            receipt.getRecords().forEach(receiptRecord -> {
                    logger.info("Updating stock record for receiptRecord: " + receiptRecord.toString());
                    StockAdapter.updateStock(receiptRecord, Optional.of(receipt.getType()));
            });
            observerList.forEach(StockUpdateListener::finished);
            observerList.clear();
            logger.info("StockListener executed successfully");
        });
    }

    public static void addObserver(StockUpdateListener o) {
        observerList.add(o);
    }
}
