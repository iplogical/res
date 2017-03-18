package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class VATTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testVATCreation() {
        assertEquals(5, getVATs().size());
    }

    @Test(expected = RollbackException.class)
    public void noSerie() {
        GuardedTransaction.Run(()->schema.getVatOne().setSerie(null));
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        GuardedTransaction.Run(()->schema.getVatOne().setName(null));
    }

    @Test(expected = RollbackException.class)
    public void noStatus() {
        GuardedTransaction.Run(()->schema.getVatOne().setStatus(null));
    }

    private void assertListSize() {
        assertEquals(5, getVATs().size());
    }

    private List<VAT> getVATs() {
        @SuppressWarnings("unchecked")
        List<VAT> entries = schema.getEntityManager().createNamedQuery(VAT.GET_TEST_VAT_RECORDS).getResultList();
        return entries;
    }
}
