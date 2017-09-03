package com.inspirationlogical.receipt.corelib.model.listeners;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransactionArchive;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReceiptArchiverListenerTest extends TestBase {

    private ReceiptAdapterBase receiptSaleOne;

    @Before
    public void createAdapters() {
        receiptSaleOne = new ReceiptAdapterBase(schema.getReceiptSaleOne());
    }

    @Test
    public void testCloneReceiptAndStoreToArchive() {
        int numberOfReceipts = GuardedTransactionArchive.runNamedQuery(Receipt.GET_RECEIPTS).size();
        int numberOfReceiptRecords = GuardedTransactionArchive.runNamedQuery(ReceiptRecord.GET_TEST_RECEIPT_RECORDS).size();
        ReceiptArchiverListener listener = new ReceiptArchiverListener();
        listener.cloneReceiptAndStoreToArchive(receiptSaleOne);
        assertEquals(numberOfReceipts + 1, GuardedTransactionArchive.runNamedQuery(Receipt.GET_RECEIPTS).size());
        assertEquals(numberOfReceiptRecords + receiptSaleOne.getAdaptee().getRecords().size(),
                GuardedTransactionArchive.runNamedQuery(ReceiptRecord.GET_TEST_RECEIPT_RECORDS).size());
    }
}
