package com.inspirationlogical.receipt.waiter.controller.reservation;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ReservationUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterReservations;
import static org.junit.Assert.assertEquals;

public class ReservationControllerTest extends TestFXBase {

    @Before
    public void enterReservationView() {
        enterReservations();
    }

    @Test
    public void testAddReservation() {
        setReservationName("TestName");
        setReservationTableNumber(1);
        setReservationGuestCount(4);
        setReservationPhoneNumber("+36307654321");
        clickOnConfirm();
        assertEquals("TestName", getReservationName(1));
        assertEquals(1, getReservationTableNumber(1));
        clickOnReservation(1);
        clickOnDelete();
        assertNoReservation();
    }

    @After
    public void toRestaurantView() {
        backToRestaurantView();
    }
}
