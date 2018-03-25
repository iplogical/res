package com.inspirationlogical.receipt.corelib.model.listeners;

/**
 * Created by Ferenc on 2017. 03. 10..
 */

import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptServicePay;

import java.util.ArrayList;
import java.util.List;

public class ReceiptAdapterListeners {

    public static  List<ReceiptServicePay.Listener> getAllListeners(){
        List<ReceiptServicePay.Listener> list = new ArrayList<>();
        list.add(new ReceiptPrinter());
        list.add(new StockListener());
        list.add(new DailyClosureListener());
        return list;
    }

    public static ReceiptServicePay.Listener getPrinterListener() {
        return new ReceiptPrinter();
    }

}
