package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_PRICE_MODIFIERS;
import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class PriceModifierTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testPriceModifierCreation() {
        assertEquals(NUMBER_OF_PRICE_MODIFIERS, getPriceModifiers().size());
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
    public void noRepeatPeriod() {
        GuardedTransaction.Run(()->
                schema.getPriceModifierOne().setRepeatPeriod(null));
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

    private List<PriceModifier> getPriceModifiers() {
        @SuppressWarnings("unchecked")
        List<PriceModifier> entries =
                schema.getEntityManager().createNamedQuery(PriceModifier.GET_TEST_PRICE_MODIFIERS).getResultList();
        return entries;
    }
}
