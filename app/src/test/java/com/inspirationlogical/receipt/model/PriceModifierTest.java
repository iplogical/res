package com.inspirationlogical.receipt.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.model.entity.PriceModifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.inspirationlogical.receipt.testsuite.ModelTest;

@Category(ModelTest.class)
public class PriceModifierTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

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
        List<PriceModifier> entries = manager.createNamedQuery(PriceModifier.GET_TEST_PRICE_MODIFIERS).getResultList();
        return entries;
    }

    private void persistPriceModifier() {
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getPriceModifierOne());
        manager.getTransaction().commit();
    }
}
