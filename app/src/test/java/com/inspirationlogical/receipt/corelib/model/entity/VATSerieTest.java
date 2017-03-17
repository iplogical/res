package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
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

    private List<VATSerie> getVATSerie() {
        @SuppressWarnings("unchecked")
        List<VATSerie> entries = schema.getEntityManager().createNamedQuery(VATSerie.GET_TEST_VAT_SERIES).getResultList();
        return entries;
    }
}
