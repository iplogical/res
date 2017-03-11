package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.Receipt;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.utils.GuardedTransaction;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Collections;

public class ReceiptAdapter extends AbstractAdapter<Receipt> {
    public  interface Listener{
        void onOpen(ReceiptAdapter receipt);
        void onClose(ReceiptAdapter receipt);
    }

    public ReceiptAdapter(Receipt receipt, EntityManager manager) {
        super(receipt, manager);
    }

    public void close(Collection<Listener> listeners) {
        GuardedTransaction.Run(manager,() -> {
            adaptee.setStatus(ReceiptStatus.CLOSED);
        });
        listeners.forEach((l) -> {l.onClose(this);});
    }

    void close(){
        close(Collections.EMPTY_LIST);
}
}
