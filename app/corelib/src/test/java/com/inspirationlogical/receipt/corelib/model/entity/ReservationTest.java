package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RESERVATIONS;
import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ReservationTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReservationCreation() {
        assertEquals(NUMBER_OF_RESERVATIONS, getReservations().size());
    }

    @Test(expected = RollbackException.class)
    public void invalidTableNumber() {
        GuardedTransaction.run(()->
                schema.getReservationOne().setTableNumber(0));
    }

    @Test(expected = RollbackException.class)
    public void noStartTime() {
        GuardedTransaction.run(()->
                schema.getReservationOne().setStartTime(null));
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        GuardedTransaction.run(()->
                schema.getReservationOne().setName(null));
    }

    private List<Reservation> getReservations() {
        @SuppressWarnings("unchecked")
        List<Reservation> entries = schema.getEntityManager().createNamedQuery(Reservation.GET_TEST_RESERVATIONS).getResultList();
        return entries;
    }
}
