package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReceiptRecordAdapterTest extends TestBase {

    private ReceiptRecordAdapter receiptSaleOneRecordOne;
    private ReceiptRecordAdapter receiptSaleOneRecordTwo;
    private Long receiptSaleOneId;
    private Long receiptSaleTwoId;

    @Before
    public void buildAdapters() {
        receiptSaleOneRecordOne = new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordOne());
        receiptSaleOneRecordTwo = new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordTwo());
        receiptSaleOneId = receiptSaleOneRecordOne.getAdaptee().getOwner().getId();
        receiptSaleTwoId = receiptSaleOneRecordTwo.getAdaptee().getOwner().getId();
    }

    @Test
    public void testDecreaseReceiptRecordDeleteOccurs() {
        long recordOneId = receiptSaleOneRecordOne.getAdaptee().getId();
        receiptSaleOneRecordOne.decreaseReceiptRecord(1);
        List<ReceiptRecord> receiptRecords = getReceiptRecordsByOwnerId(receiptSaleOneId);
        assertRecordDeleted(recordOneId, receiptRecords);
    }

    private void assertRecordDeleted(long recordOneId, List<ReceiptRecord> receiptRecords) {
        assertEquals(0, receiptRecords.stream().filter(rec -> rec.getId().equals(recordOneId)).count());
    }


    @Test
    public void testDecreaseReceiptRecordNoDeleteOccurs() {
        long recordOneId = receiptSaleOneRecordOne.getAdaptee().getId();
        receiptSaleOneRecordOne.decreaseReceiptRecord(0.5);
        List<ReceiptRecord> receiptRecords = getReceiptRecordsByOwnerId(receiptSaleOneId);
        assertRecordExists(recordOneId, receiptRecords);
        assertSoldQuantity(0.5, recordOneId, receiptRecords);
    }

    private List<ReceiptRecord> getReceiptRecordsByOwnerId(long id) {
        return GuardedTransaction.runNamedQuery(ReceiptRecord.GET_RECEIPT_RECORDS_BY_RECEIPT,
                query -> {query.setParameter("owner_id", id);
                    return query;});
    }

    private void assertRecordExists(long recordOneId, List<ReceiptRecord> receiptRecords) {
        assertEquals(1, receiptRecords.stream().filter(rec -> rec.getId().equals(recordOneId)).count());
    }

    private void assertSoldQuantity(double quantity, long recordOneId, List<ReceiptRecord> receiptRecords) {
        assertEquals(quantity, receiptRecords.stream().filter(rec -> rec.getId().equals(recordOneId)).findFirst().get().getSoldQuantity(), 0.01);
    }

    @Test
    public void testDecreaseReceiptRecordNoDeleteOccurs2() {
        long recordTwoId = receiptSaleOneRecordTwo.getAdaptee().getId();
        receiptSaleOneRecordTwo.decreaseReceiptRecord(1);
        List<ReceiptRecord> receiptRecords = getReceiptRecordsByOwnerId(receiptSaleTwoId);
        assertRecordExists(recordTwoId, receiptRecords);
        assertSoldQuantity(1, recordTwoId, receiptRecords);
    }
}
