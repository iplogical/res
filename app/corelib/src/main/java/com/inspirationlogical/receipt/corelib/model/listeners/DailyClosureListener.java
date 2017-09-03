package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureListener implements ReceiptAdapterPay.Listener {
    @Override
    public void onOpen(ReceiptAdapterPay receipt) {

    }

    @Override
    public void onClose(ReceiptAdapterBase receipt) {
        DailyClosureAdapter.getOpenDailyClosure().update(receipt);
    }
}
