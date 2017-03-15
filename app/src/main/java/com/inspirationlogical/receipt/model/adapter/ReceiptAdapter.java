package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.entity.Client;
import com.inspirationlogical.receipt.model.entity.Receipt;
import com.inspirationlogical.receipt.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.enums.ReceiptType;
import com.inspirationlogical.receipt.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.model.utils.GuardedTransaction;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class ReceiptAdapter extends AbstractAdapter<Receipt> {
    public  interface Listener{
        void onOpen(ReceiptAdapter receipt);
        void onClose(ReceiptAdapter receipt);
    }

    public static ReceiptAdapter receiptAdapterFactory(EntityManager manager, ReceiptType type) {
        return new ReceiptAdapter(Receipt.builder()
                                        .type(type)
                                        .status(ReceiptStatus.OPEN)
                                        .paymentMethod(PaymentMethod.CASH)
                                        .openTime(new GregorianCalendar())
                                        .VATSerie(VATSerieAdapter.vatSerieAdapterFactory(manager).getAdaptee())
                                        .client(getDefaultClient())
                                        .build(), manager);
    }

    private static Client getDefaultClient() {
        return Client.builder().name("client").address("dummy").TAXNumber("123").build();
    }

    public ReceiptAdapter(Receipt receipt, EntityManager manager) {
        super(receipt, manager);
    }

    public Collection<ReceiptRecordAdapter> getSoldProducts() {
        return adaptee.getRecords().stream()
                .map(receiptRecord -> new ReceiptRecordAdapter(receiptRecord, manager))
                .collect(Collectors.toList());
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
