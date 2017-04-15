package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptRecordAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;

import java.util.Optional;

import static com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider.getEntityManager;

/**
 * Created by Bálint on 2017.04.04..
 */
public class StockListener implements ReceiptAdapter.Listener {

    @Override
    public void onOpen(ReceiptAdapter receipt) {
        System.out.println("A receipt was opened.");
    }

    @Override
    public void onClose(ReceiptAdapter receipt) {
        receipt.getSoldProductsNoRefresh().forEach(receiptRecordAdapter -> StockAdapter.updateStock(receiptRecordAdapter, Optional.of(receipt.getAdaptee().getType())));
    }
}
