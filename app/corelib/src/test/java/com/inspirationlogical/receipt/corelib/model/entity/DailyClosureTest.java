package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_DAILY_CLOSURES;
import static org.junit.Assert.assertEquals;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testDailyClosureCreation() {
        assertEquals(NUMBER_OF_DAILY_CLOSURES, getDailyClosures().size());
    }

    @Test(expected = RollbackException.class)
    public void testDailyClosureNoOwner() {
        GuardedTransaction.run(() ->
                schema.getDailyClosureOne().setOwner(null));
    }

    private List<DailyClosure> getDailyClosures() {
        @SuppressWarnings("unchecked")
        List<DailyClosure> entries = schema.getEntityManager().createNamedQuery(DailyClosure.GET_LATEST_DAILY_CLOSURE).getResultList();
        return entries;
    }

}