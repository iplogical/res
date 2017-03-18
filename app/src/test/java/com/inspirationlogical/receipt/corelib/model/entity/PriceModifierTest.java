package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PriceModifierTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testPriceModifierCreation() {
        assertEquals(3, getPriceModifiers().size());
    }

    @Test(expected = RollbackException.class)
    public void noOwner() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setName(null));
    }

    @Test(expected = RollbackException.class)
    public void noType() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void noStatus() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setStatus(null));
    }

    @Test(expected = RollbackException.class)
    public void noRepeatPeriod() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setPeriod(null));
    }

    @Test(expected = RollbackException.class)
    public void noStartTime() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setStartTime(null));
    }

    @Test(expected = RollbackException.class)
    public void noEndTime() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setEndTime(null));
    }

    @Test(expected = RollbackException.class)
    public void noLimitType() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setLimitType(null));
    }

    private List<PriceModifier> getPriceModifiers() {
        @SuppressWarnings("unchecked")
        List<PriceModifier> entries =
                schema.getEntityManager().createNamedQuery(PriceModifier.GET_TEST_PRICE_MODIFIERS).getResultList();
        return entries;
    }
}
