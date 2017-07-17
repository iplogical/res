package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureListener implements ReceiptAdapter.Listener {
    @Override
    public void onOpen(ReceiptAdapter receipt) {

    }

    @Override
    public void onClose(ReceiptAdapter receipt) {
        BackgroundThread.execute(() -> {
            DailyClosureAdapter.getOpenDailyClosure().update(receipt);
            System.out.println(Thread.currentThread().getName() + ": DailyClosureListener executed successfully");
        });
    }
}
