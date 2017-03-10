package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.Receipt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface ReceiptAdapter extends AbstractAdapter<Receipt> {
    public  interface Listener{
        void onOpen(ReceiptAdapter receipt);
        void onClose(ReceiptAdapter receipt);
    }

    void close(Collection<Listener> listeners);
    default void close(){
        close(Collections.EMPTY_LIST);
}
}
