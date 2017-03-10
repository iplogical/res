package com.inspirationlogical.receipt.model.adapter;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
import java.util.ArrayList;
import java.util.List;

public class ReceiptAdapterListeners {

    public static  List<ReceiptAdapter.Listener> getObservers(){
        List<ReceiptAdapter.Listener> list = new ArrayList<>();
        list.add(new ReceiptPrinter());
        return list;
    }
}
