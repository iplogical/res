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
        GuardedTransaction.run(()->
                schema.getPriceModifierOne().setOwner(null));
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        GuardedTransaction.run(()->
                schema.getPriceModifierOne().setName(null));
    }

    @Test(expected = RollbackException.class)
    public void noType() {
        GuardedTransaction.run(()->
                schema.getPriceModifierOne().setType(null));
    }

    @Test(expected = RollbackException.class)
    public void noRepeatPeriod() {
        GuardedTransaction.run(()->
                schema.getPriceModifierOne().setRepeatPeriod(null));
    }

    @Test(expected = RollbackException.class)
    public void noStartTime() {
        GuardedTransaction.run(()->
                schema.getPriceModifierOne().setStartDate(null));
    }

    @Test(expected = RollbackException.class)
    public void noEndTime() {
        GuardedTransaction.run(()->
                schema.getPriceModifierOne().setEndDate(null));
    }

    private List<PriceModifier> getPriceModifiers() {
        @SuppressWarnings("unchecked")
        List<PriceModifier> entries =
                schema.getEntityManager().createNamedQuery(PriceModifier.GET_TEST_PRICE_MODIFIERS).getResultList();
        return entries;
    }
}
