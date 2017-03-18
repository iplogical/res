package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReservationTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReservationCreation() {
        assertEquals(2, getReservations().size());
    }

    @Test(expected = RollbackException.class)
    public void invalidTableNumber() {
        GuardedTransaction.Run(()->
                schema.getReservationOne().setTableNumber(0));
    }

    @Test(expected = RollbackException.class)
    public void noStartTime() {
        GuardedTransaction.Run(()->
                schema.getReservationOne().setStartTime(null));
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        GuardedTransaction.Run(()->
                schema.getReservationOne().setName(null));
    }

    private List<Reservation> getReservations() {
        @SuppressWarnings("unchecked")
        List<Reservation> entries = schema.getEntityManager().createNamedQuery(Reservation.GET_TEST_RESERVATIONS).getResultList();
        return entries;
    }
}
