package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StockTest {

    private EntityManager manager;

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
        manager = schema.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getStockOne());
        manager.getTransaction().commit();
    }
}
 