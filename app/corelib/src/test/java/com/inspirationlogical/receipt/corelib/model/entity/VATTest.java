package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_VAT_RECORDS;
import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;

public class VATTest extends TestBase {

    @Test
    public void testVATCreation() {
        assertEquals(NUMBER_OF_VAT_RECORDS, getVATs().size());
    }

    @Test(expected = RollbackException.class)
    public void noSerie() {
        GuardedTransaction.run(()->schema.getVatOne().setSerie(null));
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        GuardedTransaction.run(()->schema.getVatOne().setName(null));
    }

    @Test(expected = RollbackException.class)
    public void noStatus() {
        GuardedTransaction.run(()->schema.getVatOne().setStatus(null));
    }

    private List<VAT> getVATs() {
        @SuppressWarnings("unchecked")
        List<VAT> entries = schema.getEntityManager().createNamedQuery(VAT.GET_TEST_VAT_RECORDS).getResultList();
        return entries;
    }
}
