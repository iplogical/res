package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
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
        assertEquals(3, getStocks().size());
    }

    @Test
    public void stockOwner() {
        assertEquals("productFour", getStocks().get(0).getOwner().getLongName());
    }

    @Test(expected = RollbackException.class)
    public void stockWithoutOwner() {
        GuardedTransaction.Run(schema.getEntityManager(),()->
                schema.getStockOne().setOwner(null));

    }

    private List<Stock> getStocks() {
        @SuppressWarnings("unchecked")
        List<Stock> entries = schema.getEntityManager().createNamedQuery(Stock.GET_TEST_STOCKS).getResultList();
        return entries;
    }
}
 