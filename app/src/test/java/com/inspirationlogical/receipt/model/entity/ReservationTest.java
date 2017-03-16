package com.inspirationlogical.receipt.model.entity;

import com.inspirationlogical.receipt.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.model.EntityManagerFactoryRule;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReservationTest {

    private EntityManager manager;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testReservationCreation() {
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void invalidTableNumber() {
        schema.getReservationOne().setTableNumber(0);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noStartTime() {
        schema.getReservationOne().setStartTime(null);
        assertListSize();
    }

    @Test(expected = RollbackException.class)
    public void noName() {
        schema.getReservationOne().setName(null);
        assertListSize();
    }

    private void assertListSize() {
        assertEquals(2, persistReservationAndGetList().size());
    }

    private List<Reservation> persistReservationAndGetList() {
        persistReservation();
        @SuppressWarnings("unchecked")
        List<Reservation> entries = manager.createNamedQuery(Reservation.GET_TEST_RESERVATIONS).getResultList();
        return entries;
    }

    private void persistReservation() {
        manager = schema.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getReservationOne());
        manager.getTransaction().commit();
    }


}
