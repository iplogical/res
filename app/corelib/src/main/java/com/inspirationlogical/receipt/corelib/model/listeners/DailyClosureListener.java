package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptServicePay;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureListener implements ReceiptServicePay.Listener {

    @Override
    public void onClose(Receipt receipt) {
        DailyClosureAdapter.getOpenDailyClosure().update(receipt);
    }
}
