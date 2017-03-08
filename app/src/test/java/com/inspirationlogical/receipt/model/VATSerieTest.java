package com.inspirationlogical.receipt.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;

public class VATSerieTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testVATSerieCreation() {
        assertListSize();
    }

    @Test
    public void testVatValueNumber() {
        assertEquals(5, persistVATSerieAndGetList().get(0).getVat().size());
    }

    private void assertListSize() {
        assertEquals(1, persistVATSerieAndGetList().size());
    }

    private List<VATSerie> persistVATSerieAndGetList() {
        persistVATSerie();
        @SuppressWarnings("unchecked")
        List<VATSerie> entries = manager.createNamedQuery(VATSerie.GET_TEST_VAT_SERIES).getResultList();
        return entries;
    }

    private void persistVATSerie() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getVatSerie());
        manager.getTransaction().commit();
    }
}
