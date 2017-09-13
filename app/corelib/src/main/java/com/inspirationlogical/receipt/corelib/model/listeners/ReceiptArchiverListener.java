package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterPay;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransactionArchive;

import java.util.List;

public class ReceiptArchiverListener implements ReceiptAdapterPay.Listener {

    @Override
    public void onOpen(ReceiptAdapterPay receipt) {

    }

    @Override
    public void onClose(Receipt receipt) {
//        BackgroundThread.execute(() -> {
//            cloneReceiptAndStoreToArchive(receipt);
//            GuardedTransaction.detach(receipt.getAdaptee());
//            System.out.println(Thread.currentThread().getName() + ": ReceiptArchiverListener executed successfully");
//        });
    }

    void cloneReceiptAndStoreToArchive(ReceiptAdapterBase receipt) {
        Receipt newReceipt = cloneReceipt(receipt);
        cloneReceiptRecords(receipt.getAdaptee().getRecords(), newReceipt);
        GuardedTransactionArchive.persist(newReceipt);
    }

    private Receipt cloneReceipt(ReceiptAdapterBase receipt) {
        List<Table> tables = GuardedTransactionArchive.runNamedQuery(Table.GET_TABLE_BY_NUMBER, query ->
            query.setParameter("number", receipt.getAdaptee().getOwner().getNumber()));
        List<VATSerie> vatSeries = GuardedTransactionArchive.runNamedQuery((VATSerie.GET_VAT_SERIE));
        Receipt newReceipt = Receipt.cloneReceipt(receipt.getAdaptee());
        newReceipt.setOwner(tables.get(0));
        newReceipt.setVATSerie(vatSeries.get(0));
        return newReceipt;
    }

    private void cloneReceiptRecords(List<ReceiptRecord> records, Receipt newReceipt) {
        records.forEach(record -> {
            ReceiptRecord newRecord = ReceiptRecord.cloneReceiptRecord(record);
            newRecord.setOwner(newReceipt);
            newReceipt.getRecords().add(newRecord);
            List<Product> products = GuardedTransactionArchive.runNamedQuery(Product.GET_PRODUCT_BY_NAME, query ->
                    query.setParameter("longName", record.getProduct().getLongName()));
            newRecord.setProduct(products.get(0));
        });
    }
}
