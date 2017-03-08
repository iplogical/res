package com.inspirationlogical.receipt.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.inspirationlogical.receipt.testsuite.ModelTest;

@Category(ModelTest.class)
public class VATTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

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
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getVatOne());
        manager.getTransaction().commit();
    }
}
