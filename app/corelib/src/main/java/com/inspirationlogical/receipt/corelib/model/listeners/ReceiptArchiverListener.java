package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.utils.BackgroundThread;
import com.inspirationlogical.receipt.corelib.model.utils.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import javax.persistence.EntityManager;
import java.util.List;

public class ReceiptArchiverListener implements ReceiptAdapter.Listener {

    @Override
    public void onOpen(ReceiptAdapter receipt) {

    }

    @Override
    public void onClose(ReceiptAdapter receipt) {
        BackgroundThread.execute(() -> {
            cloneAndStoreToArchive(receipt);
        });
    }

    private void cloneAndStoreToArchive(ReceiptAdapter receipt) {
        Receipt newReceipt = cloneReceipt(receipt);
        cloneReceiptRecords(receipt.getAdaptee().getRecords(), newReceipt);
        GuardedTransaction.persistArchive(newReceipt);
        System.out.println(Thread.currentThread().getName() + ": ReceiptArchiverListener executed successfully");
    }

    private Receipt cloneReceipt(ReceiptAdapter receipt) {
        List<Table> tables = GuardedTransaction.runNamedQueryArchive(Table.GET_TABLE_BY_NUMBER, query ->
            query.setParameter("number", receipt.getAdaptee().getOwner().getNumber()));
        List<VATSerie> vatSeries = GuardedTransaction.runNamedQueryArchive((VATSerie.GET_VAT_SERIE));
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
            List<Product> products = GuardedTransaction.runNamedQueryArchive(Product.GET_PRODUCT_BY_NAME, query ->
                    query.setParameter("longName", record.getProduct().getLongName()));
            newRecord.setProduct(products.get(0));
        });
    }
}
