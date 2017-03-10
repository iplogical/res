package com.inspirationlogical.receipt.model.adapter;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.Receipt;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.utils.GuardedTransaction;

import java.util.Collection;

public class ReceiptAdapterImpl extends AbstractAdapterImpl<Receipt>
    implements ReceiptAdapter {

    public ReceiptAdapterImpl(Receipt receipt, EntityManager manager) {
        super(receipt, manager);
    }

    @Override
    public void close(Collection<Listener> listeners) {
        GuardedTransaction.Run(manager,() -> {
            adaptee.setStatus(ReceiptStatus.CLOSED);
            manager.persist(adaptee);
        });
        listeners.forEach((l) -> {l.onClose(this);});
    }

}
