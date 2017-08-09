package com.inspirationlogical.receipt.corelib.model.entity;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_RESERVATIONS;
import static org.junit.Assert.assertEquals;

import java.util.List;
import javax.persistence.RollbackException;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ReservationTest extends TestBase {

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
        List<Reservation> entries = schema.getEntityManager().createNamedQuery(Reservation.GET_RESERVATIONS).getResultList();
        return entries;
    }
}
