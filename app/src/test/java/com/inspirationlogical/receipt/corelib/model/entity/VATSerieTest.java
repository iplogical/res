package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VATSerieTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testVATSerieCreation() {
        assertEquals(1, getVATSerie().size());
    }

    @Test
    public void testVatValueNumber() {
        assertEquals(5, getVATSerie().get(0).getVat().size());
    }

    @Test(expected = RollbackException.class)
    public void noStatus() {
        GuardedTransaction.Run(schema.getEntityManager(),()->schema.getVatSerie().setStatus(null));
    }

    private List<VATSerie> getVATSerie() {
        @SuppressWarnings("unchecked")
        List<VATSerie> entries = schema.getEntityManager().createNamedQuery(VATSerie.GET_TEST_VAT_SERIES).getResultList();
        return entries;
    }
}
