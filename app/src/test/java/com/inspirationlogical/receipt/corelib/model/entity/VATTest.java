package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VATTest {

    private EntityManager manager;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testVATCreation() {
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noSerie() {
        schema.getVatOne().setSerie(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        schema.getVatOne().setName(null);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(5, persistVATAndGetList().size());
    }

    private List<VAT> persistVATAndGetList() {
        persistVAT();
        @SuppressWarnings("unchecked")
        List<VAT> entries = manager.createNamedQuery(VAT.GET_TEST_VAT_RECORDS).getResultList();
        return entries;
    }

    private void persistVAT() {
        manager = schema.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getVatOne());
        manager.getTransaction().commit();
    }
}
