package com.inspirationlogical.receipt.model.entity;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PriceModifierTest {

    private EntityManager manager;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testPriceModifierCreation() {
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noOwner() {
        schema.getPriceModifierOne().setOwner(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        schema.getPriceModifierOne().setName(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noType() {
        schema.getPriceModifierOne().setType(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noStatus() {
        schema.getPriceModifierOne().setStatus(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noRepeatPeriod() {
        schema.getPriceModifierOne().setPeriod(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noStartTime() {
        schema.getPriceModifierOne().setStartTime(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noEndTime() {
        schema.getPriceModifierOne().setEndTime(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noLimitType() {
        schema.getPriceModifierOne().setLimitType(null);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(3, persistPriceModifierAndGetList().size());
    }

    private List<PriceModifier> persistPriceModifierAndGetList() {
        persistPriceModifier();
        @SuppressWarnings("unchecked")
        List<PriceModifier> entries = schema.getEntityManager().createNamedQuery(PriceModifier.GET_TEST_PRICE_MODIFIERS).getResultList();
        return entries;
    }

    private void persistPriceModifier() {
        manager = schema.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getPriceModifierOne());
        manager.getTransaction().commit();
    }
}
