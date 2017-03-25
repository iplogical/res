package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RECEIPT_RECORDS;
import static org.junit.Assert.assertEquals;

public class ReceiptRecordTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReceiptRecordCreation() {
        assertEquals(NUMBER_OF_RECEIPT_RECORDS, getReceiptRecords().size());
    }

    @Test(expected = RollbackException.class)
    public void testNoType() {
        GuardedTransaction.Run(()->
            schema.getReceiptRecordSaleOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void testNoOwner() {
        GuardedTransaction.Run(()->
            schema.getReceiptRecordSaleOne().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void testNoName() {
        GuardedTransaction.Run(()->
            schema.getReceiptRecordSaleOne().setName(""));
    }

    @Test(expected = RollbackException.class)
    public void testNoProduct() {
        GuardedTransaction.Run(()->
            schema.getReceiptRecordSaleOne().setProduct(null));
    }

    @Test(expected = RollbackException.class)
    public void testOtherReceiptRecordHasProduct() {
        GuardedTransaction.Run(()->
            schema.getReceiptRecordOther().setProduct(schema.getProductOne()));
    }

    @Test(expected = RollbackException.class)
    public void testPercentDiscountMoreThanHundred() {
        GuardedTransaction.Run(()->
            schema.getReceiptRecordSaleOne().setDiscountPercent(150D));
    }

    private List<ReceiptRecord> getReceiptRecords() {
        @SuppressWarnings("unchecked")
        List<ReceiptRecord> entries = schema.getEntityManager().createNamedQuery(ReceiptRecord.GET_TEST_RECEIPTS_RECORDS).getResultList();
        return entries;
    }
}
