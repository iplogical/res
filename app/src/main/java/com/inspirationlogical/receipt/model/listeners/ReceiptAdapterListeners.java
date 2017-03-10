package com.inspirationlogical.receipt.model.listeners;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
import com.inspirationlogical.receipt.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.model.listeners.ReceiptPrinter;

import java.util.ArrayList;
import java.util.List;

public class ReceiptAdapterListeners {

    public static  List<ReceiptAdapter.Listener> getObservers(){
        List<ReceiptAdapter.Listener> list = new ArrayList<>();
        list.add(new ReceiptPrinter());
        return list;
    }
}
