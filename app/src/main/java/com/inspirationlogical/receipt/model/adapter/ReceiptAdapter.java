package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.entity.Receipt;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.model.utils.GuardedTransaction;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.Collection;

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
            adaptee.setClosureTime(Calendar.getInstance());
            listeners.forEach((l) -> {l.onClose(this);});
        });

    }

    void close(){
        close(ReceiptAdapterListeners.getAllListeners());
}
}
