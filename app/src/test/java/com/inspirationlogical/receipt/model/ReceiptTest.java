package com.inspirationlogical.receipt.model;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;

public class ReceiptTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReceiptCreation() {
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(4, persistReceiptAndGetList().size());
    }

    private List<Receipt> persistReceiptAndGetList() {
        persistReceipt();
        @SuppressWarnings("unchecked")
        List<Receipt> entries = manager.createNamedQuery(Receipt.GET_TEST_RECEIPTS).getResultList();
        return entries;
    }

    private void persistReceipt() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getReceiptOne());
        manager.getTransaction().commit();
    }
}
