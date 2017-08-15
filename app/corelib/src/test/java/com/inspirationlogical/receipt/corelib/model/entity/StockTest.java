package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_STOCKS;
import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;

public class StockTest extends TestBase {

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
        GuardedTransaction.run(()->
                schema.getStockOne().setOwner(null));

    }

    private List<Stock> getStocks() {
        @SuppressWarnings("unchecked")
        List<Stock> entries = schema.getEntityManager().createNamedQuery(Stock.STOCK_GET_ITEMS).getResultList();
        return entries;
    }
}
 