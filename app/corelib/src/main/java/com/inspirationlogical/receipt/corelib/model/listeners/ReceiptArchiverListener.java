package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptHistorical;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecordHistorical;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ReceiptArchiverListener implements ReceiptAdapter.Listener {

    @Override
    public void onOpen(ReceiptAdapter receipt) {

    }

    @Override
    public void onClose(ReceiptAdapter receipt) {
        BackgroundThread.execute(() -> {
            ReceiptHistorical copy = new ReceiptHistorical(receipt.getAdaptee());
            receipt.getAdaptee().getRecords().forEach(receiptRecord -> {
                copy.getRecords().add(new ReceiptRecordHistorical(receiptRecord, copy));
            });
            GuardedTransaction.persist(copy);
            System.out.println(Thread.currentThread().getName() + ": ReceiptArchiverListener executed successfully");
        });
    }
}
