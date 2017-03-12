package com.inspirationlogical.receipt.model;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.inspirationlogical.receipt.testsuite.ModelTest;

import static org.junit.Assert.*;

@Category(ModelTest.class)
public class ReceiptRecordTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReceiptRecordCreation() {
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testNoType() {
        schema.getReceiptRecordSaleOne().setType(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testNoOwner() {
        schema.getReceiptRecordSaleOne().setOwner(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testNoName() {
        schema.getReceiptRecordSaleOne().setName("");
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testNoProduct() {
        schema.getReceiptRecordSaleOne().setProduct(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testOtherReceiptRecordHasProduct() {
        schema.getReceiptRecordOther().setProduct(schema.getProductOne());
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testAbsoluteAndPercentDiscountSimultaneously() {
        schema.getReceiptRecordSaleOne().setDiscountAbsolute(1000D);
        schema.getReceiptRecordSaleOne().setDiscountPercent(10D);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void testPercentDiscountMoreThanHundred() {
        schema.getReceiptRecordSaleOne().setDiscountPercent(150D);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(5, persistReceiptRecordAndGetList().size());
    }

    private List<ReceiptRecord> persistReceiptRecordAndGetList() {
        persistReceiptRecord();
        @SuppressWarnings("unchecked")
        List<ReceiptRecord> entries = manager.createNamedQuery(ReceiptRecord.GET_TEST_RECEIPTS_RECORDS).getResultList();
        return entries;
    }

    private void persistReceiptRecord() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getReceiptRecordSaleOne());
        manager.getTransaction().commit();
    }

}
