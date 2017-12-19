package com.inspirationlogical.receipt.corelib.model.listeners;

/**
 * Created by Ferenc on 2017. 03. 10..
 */

import java.util.ArrayList;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;

public class ReceiptAdapterListeners {

    public static  List<ReceiptAdapterPay.Listener> getAllListeners(){
        List<ReceiptAdapterPay.Listener> list = new ArrayList<>();
        list.add(new ReceiptPrinter());
        list.add(new StockListener());
        list.add(new DailyClosureListener());
        return list;
    }

    public static ReceiptAdapterPay.Listener getPrinterListener() {
        return new ReceiptPrinter();
    }

}
