package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.Receipt;

public interface ReceiptAdapter extends AbstractAdapter<Receipt> {
    public  interface Listener{
        void onOpen(ReceiptAdapter receipt);
        void onClose(ReceiptAdapter receipt);
    }
}
