package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchema.NUMBER_OF_VAT_RECORDS;
import static com.inspirationlogical.receipt.corelib.model.BuildTestSchema.NUMBER_OF_VAT_SERIE;
import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.AbstractTest;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class VATSerieTest extends AbstractTest {

    @Test
    public void testVATSerieCreation() {
        assertEquals(NUMBER_OF_VAT_SERIE, getVATSerie().size());
    }

    @Test
    public void testVatValueNumber() {
        assertEquals(NUMBER_OF_VAT_RECORDS, getVATSerie().get(0).getVat().size());
    }

    @Test(expected = RollbackException.class)
    public void noStatus() {
        GuardedTransaction.run(()->schema.getVatSerie().setStatus(null));
    }

    private List<VATSerie> getVATSerie() {
        @SuppressWarnings("unchecked")
        List<VATSerie> entries = schema.getEntityManager().createNamedQuery(VATSerie.GET_TEST_VAT_SERIES).getResultList();
        return entries;
    }
}
