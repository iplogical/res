package com.inspirationlogical.receipt.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.RollbackException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.inspirationlogical.receipt.testsuite.ModelTest;

@Category(ModelTest.class)
public class ReservationTest {

    private EntityManager manager;

    @Rule
    public final EntityManagerFactoryRule factory = new EntityManagerFactoryRule();

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
        manager = factory.getEntityManager();
        manager.getTransaction().begin();
        manager.persist(schema.getReservationOne());
        manager.getTransaction().commit();
    }


}
