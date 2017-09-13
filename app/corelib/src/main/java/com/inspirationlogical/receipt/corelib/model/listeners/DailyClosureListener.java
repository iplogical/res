package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureListener implements ReceiptAdapterPay.Listener {
    @Override
    public void onOpen(ReceiptAdapterPay receipt) {

    }

    @Override
    public void onClose(Receipt receipt) {
        DailyClosureAdapter.getOpenDailyClosure().update(receipt);
    }
}
