package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_STOCKS;
import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class StockTest {

    private EntityManager manager;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testStockCreation() {
        assertEquals(NUMBER_OF_STOCKS, getStocks().size());
    }

    @Test
    public void stockOwner() {
        assertEquals("productRecipeElementOne", getStocks().get(0).getOwner().getLongName());
    }

    @Test(expected = RollbackException.class)
    public void stockWithoutOwner() {
        GuardedTransaction.Run(()->
                schema.getStockOne().setOwner(null));

    }

    private List<Stock> getStocks() {
        @SuppressWarnings("unchecked")
        List<Stock> entries = schema.getEntityManager().createNamedQuery(Stock.STOCK_GET_ITEMS).getResultList();
        return entries;
    }
}
 