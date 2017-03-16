package com.inspirationlogical.receipt.corelib.model.listeners;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReceiptAdapterListeners {

    public static  List<ReceiptAdapter.Listener> getAllListeners(){
        List<ReceiptAdapter.Listener> list = new ArrayList<>();
        list.add(new ReceiptPrinter());
        return list;
    }
}
