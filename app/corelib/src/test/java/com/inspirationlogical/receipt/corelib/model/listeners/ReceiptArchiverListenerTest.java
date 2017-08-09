package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.adapter.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReceiptArchiverListenerTest extends TestBase {

    private ReceiptAdapter receiptSaleOne;

    @Before
    public void createAdapters() {
        receiptSaleOne = new ReceiptAdapter(schema.getReceiptSaleOne());
    }

    @Test
    public void testCloneReceiptAndStoreToArchive() {
        int numberOfReceipts = GuardedTransaction.runNamedQueryArchive(Receipt.GET_RECEIPTS).size();
        int numberOfReceiptRecords = GuardedTransaction.runNamedQueryArchive(ReceiptRecord.GET_TEST_RECEIPT_RECORDS).size();
        ReceiptArchiverListener listener = new ReceiptArchiverListener();
        listener.cloneReceiptAndStoreToArchive(receiptSaleOne);
        assertEquals(numberOfReceipts + 1, GuardedTransaction.runNamedQueryArchive(Receipt.GET_RECEIPTS).size());
        assertEquals(numberOfReceiptRecords + receiptSaleOne.getAdaptee().getRecords().size(),
                GuardedTransaction.runNamedQueryArchive(ReceiptRecord.GET_TEST_RECEIPT_RECORDS).size());
    }
}
