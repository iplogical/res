package com.inspirationlogical.receipt.model;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

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

    private void assertListSize() {
        assertEquals(4, persistReceiptRecordAndGetList().size());
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
        manager.persist(schema.getProductOne());
        manager.persist(schema.getReceiptRecordSaleOne());
        manager.getTransaction().commit();
    }

}
