package com.inspirationlogical.receipt.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

public class StockTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testStockCreation() {
        assertListSize();
    }

    @Test
    public void stockOwner() {
        assertEquals("productFour", persistStockAndGetList().get(0).getOwner().getLongName());
    }

    @Test(expected = RollbackException.class)
    public void stockWithoutOwner() {
        schema.getStockOne().setOwner(null);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(3, persistStockAndGetList().size());
    }

    private List<Stock> persistStockAndGetList() {
        persistRecipe();
        @SuppressWarnings("unchecked")
        List<Stock> entries = manager.createNamedQuery(Stock.GET_TEST_STOCKS).getResultList();
        return entries;
    }

    private void persistRecipe() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getStockOne());
        manager.getTransaction().commit();
    }
}
 