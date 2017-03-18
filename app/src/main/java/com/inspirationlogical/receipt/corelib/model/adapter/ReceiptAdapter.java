package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.Client;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import com.inspirationlogical.receipt.corelib.model.listeners.ReceiptAdapterListeners;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

public class ReceiptAdapter extends AbstractAdapter<Receipt> {

    public  interface Listener{
        void onOpen(ReceiptAdapter receipt);
        void onClose(ReceiptAdapter receipt);
    }

    public static ReceiptAdapter receiptAdapterFactory(ReceiptType type) {
        return new ReceiptAdapter(Receipt.builder()
                                        .type(type)
                                        .status(ReceiptStatus.OPEN)
                                        .paymentMethod(PaymentMethod.CASH)
                                        .openTime(new GregorianCalendar())
                                        .VATSerie(VATSerieAdapter.vatSerieAdapterFactory().getAdaptee())
                                        .client(getDefaultClient())
                                        .build());
    }

    private static Client getDefaultClient() {
        // FIXME
        return Client.builder().name("client").address("dummy").TAXNumber("123").build();
    }

    public ReceiptAdapter(Receipt receipt) {
        super(receipt);
    }

    public void sellProduct(ProductAdapter productAdapter, int amount, PaymentParams paymentParams) {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            ReceiptRecord record = ReceiptRecord.builder()
                    .product(productAdapter.getAdaptee())
                    .type(paymentParams.getReceiptRecordType())
                    .created(new GregorianCalendar())
                    .name(productAdapter.getAdaptee().getLongName())
                    .soldQuantity(amount)
                    .purchasePrice(productAdapter.getAdaptee().getPurchasePrice())
                    .salePrice(productAdapter.getAdaptee().getSalePrice())
                    .VAT(VATAdapter.getVatByName(paymentParams.getReceiptRecordType(), VATStatus.VALID).getAdaptee().getVAT())
                    .discountAbsolute(paymentParams.getDiscountAbsolute())
                    .discountPercent(paymentParams.getDiscountPercent())
                    .build();
            record.setOwner(adaptee);
            adaptee.getRecords().add(record);
            // FIXME: Persist new ReciptRecord?
        });
    }

    public Collection<ReceiptRecordAdapter> getSoldProducts() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        return adaptee.getRecords().stream()
                .map(receiptRecord -> new ReceiptRecordAdapter(receiptRecord))
                .collect(Collectors.toList());
    }

    public void close(Collection<Listener> listeners) {
        GuardedTransaction.Run(() -> {
            adaptee.setStatus(ReceiptStatus.CLOSED);
            adaptee.setClosureTime(Calendar.getInstance());
            listeners.forEach((l) -> {l.onClose(this);});
        });

    }

    void close(){
        close(ReceiptAdapterListeners.getAllListeners());
}
}
