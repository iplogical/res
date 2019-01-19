package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.jaxb.Receipt;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptServicePay;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReceiptCloseListeners {

    @Autowired
    DailyClosureListener dailyClosureListener;

    @Autowired
    private ReceiptPrinter receiptPrinter;

    @Autowired
    private StockListener stockListener;

    public List<ReceiptServicePay.Listener> getAllListeners(){
        List<ReceiptServicePay.Listener> list = new ArrayList<>();
        list.add(dailyClosureListener);
        list.add(receiptPrinter);
        list.add(stockListener);
        return list;
    }

    public ReceiptPrinter getPrinterListener() {
        return receiptPrinter;
    }

}
