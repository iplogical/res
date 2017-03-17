package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReceiptRecordTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReceiptRecordCreation() {
        assertEquals(5, getReceiptRecords().size());
    }

    @Test(expected = RollbackException.class)
    public void testNoType() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getReceiptRecordSaleOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void testNoOwner() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getReceiptRecordSaleOne().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void testNoName() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getReceiptRecordSaleOne().setName(""));
    }

    @Test(expected = RollbackException.class)
    public void testNoProduct() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getReceiptRecordSaleOne().setProduct(null));
    }

    @Test(expected = RollbackException.class)
    public void testOtherReceiptRecordHasProduct() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getReceiptRecordOther().setProduct(schema.getProductOne()));
    }

    @Test(expected = RollbackException.class)
    public void testAbsoluteAndPercentDiscountSimultaneously() {
        GuardedTransaction.Run(schema.getEntityManager(),()->{
            schema.getReceiptRecordSaleOne().setDiscountAbsolute(1000);
            schema.getReceiptRecordSaleOne().setDiscountPercent(10D);});
    }

    @Test(expected = RollbackException.class)
    public void testPercentDiscountMoreThanHundred() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
            schema.getReceiptRecordSaleOne().setDiscountPercent(150D));
    }

    private List<ReceiptRecord> getReceiptRecords() {
        @SuppressWarnings("unchecked")
        List<ReceiptRecord> entries = schema.getEntityManager().createNamedQuery(ReceiptRecord.GET_TEST_RECEIPTS_RECORDS).getResultList();
        return entries;
    }
}
