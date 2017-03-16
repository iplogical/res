package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VATSerieTest {

    private EntityManager manager;

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
        manager = schema.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getVatSerie());
        manager.getTransaction().commit();
    }
}
