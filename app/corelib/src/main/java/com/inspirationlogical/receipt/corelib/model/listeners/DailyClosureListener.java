package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptServicePay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyClosureListener implements ReceiptServicePay.Listener {

    @Autowired
    private DailyClosureService dailyClosureService;

    @Override
    public void onClose(Receipt receipt) {
        dailyClosureService.update(receipt);
    }
}
