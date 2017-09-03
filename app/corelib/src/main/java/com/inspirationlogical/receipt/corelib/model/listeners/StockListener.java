package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;

import java.util.*;

/**
 * Created by Bálint on 2017.04.04..
 */
public class StockListener implements ReceiptAdapterPay.Listener {

    @FunctionalInterface
    public interface StockUpdateListener {
        void finished();
    }
    private static List<StockUpdateListener> observerList = new ArrayList<>();

    @Override
    public void onOpen(ReceiptAdapterPay receipt) {

    }

    @Override
    public void onClose(ReceiptAdapterBase receipt) {
        BackgroundThread.execute(() -> {
            receipt.getSoldProducts().forEach(receiptRecordAdapter ->
                    StockAdapter.updateStock(receiptRecordAdapter.getAdaptee(), Optional.of(receipt.getAdaptee().getType())));
            observerList.forEach(StockUpdateListener::finished);
            observerList.clear();
            System.out.println(Thread.currentThread().getName() + ": StockListener executed successfully");
        });
    }

    public static void addObserver(StockUpdateListener o) {
        observerList.add(o);
    }
}
