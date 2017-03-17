package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;

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

    public void sellProduct(ProductAdapter productAdapter, int amount, PaymentParams paymentParams) {
        GuardedTransaction.Run(manager, () -> manager.refresh(adaptee));
        GuardedTransaction.Run(manager,() -> {
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(productAdapter.getAdaptee())
                    .type(paymentParams.getReceiptRecordType())
                    .created(new GregorianCalendar())
                    .name(productAdapter.getAdaptee().getLongName())
                    .soldQuantity(amount)
                    .purchasePrice(productAdapter.getAdaptee().getPurchasePrice())
                    .salePrice(productAdapter.getAdaptee().getSalePrice())
                    .VAT(VATAdapter.getVatByName(manager, paymentParams.getReceiptRecordType(), VATStatus.VALID).getAdaptee().getVAT())
                    .discountAbsolute(paymentParams.getDiscountAbsolute())
                    .discountPercent(paymentParams.getDiscountPercent())
                    .build();
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
            manager.persist(adaptee);
        });
    }

    public Collection<ReceiptRecordAdapter> getSoldProducts() {
        GuardedTransaction.Run(manager,() -> manager.refresh(adaptee));
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
