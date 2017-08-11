package com.inspirationlogical.receipt.waiter.controller.reservation;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.*;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.verifyErrorMessageWithParam;
import static com.inspirationlogical.receipt.waiter.utility.PayUtils.pay;
import static com.inspirationlogical.receipt.waiter.utility.ReservationUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterReservations;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterSaleView;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.enterPaymentView;
import static org.junit.Assert.assertEquals;

public class ReservationControllerTest extends TestFXBase {

    @Before
    public void enterReservationView() {
        enterReservations();
    }

    @Test
    public void testAddReservation() {
        addReservation("NewReservation", 4, 4);
        assertEquals("NewReservation", getReservationName(3));
        assertEquals(4, getReservationTableNumber(3));
        assertEquals(4, getReservationGuestCount(3));
        assertNumberOfReservations(3);
        clickOnReservation(3);
        clickOnDelete();
        assertNumberOfReservations(2);
    }

    @Test
    public void testModifyReservation() {
        clickOnReservation(1);
        setReservationName("New Name of the Reservation");
        setReservationTableNumber(1);
        setReservationGuestCount(8);
        setReservationPhoneNumber("+36307654321");
        clickOnUpdate();
        assertEquals("New Name of the Reservation", getReservationName(1));
        assertEquals(1, getReservationTableNumber(1));
        assertEquals(8, getReservationGuestCount(1));
        assertNumberOfReservations(2);
    }

    @Test
    public void testSelectReservationFillsTheForm() {
        clickOnReservation(1);
        assertEquals(RESERVATION_ONE_NAME, getReservationName());
        assertEquals(RESERVATION_ONE_NOTE, getReservationNote());
        assertEquals(RESERVATION_ONE_TABLE_NUMBER, getReservationTableNumber());
    }

    @Test
    public void testDeleteReservations() {
        assertNumberOfReservations(2);
        addReservation("NewReservation1", 4, 4);
        addReservation("NewReservation2", 5, 4);
        assertNumberOfReservations(4);
        clickOnReservation(3);
        clickOnDelete();
        assertNumberOfReservations(3);
        clickOnReservation(3);
        clickOnDelete();
        assertNumberOfReservations(2);
    }

    @Test
    public void testOpenTableOfReservation() {
        addReservation("TestName", Integer.valueOf(RESERVATION_TEST_TABLE), 4);
        clickOnReservation(3);
        clickOnOpenTable();
        enterSaleView(RESERVATION_TEST_TABLE);
        enterPaymentView();
        pay();
        enterReservations();
        clickOnReservation(3);
        clickOnDelete();
    }

    @Test
    public void openTableOfReservationNoSuchTable() {
        addReservation("TestName", 11, 4);
        clickOnReservation(3);
        clickOnOpenTable();
        verifyErrorMessageWithParam("TableDoesNotExist", "11");
        enterReservations();
        clickOnReservation(3);
        clickOnDelete();
    }

    @Test
    public void openTableOfReservationAlreadyOpen() {
        addReservation("TestName", Integer.valueOf(SALE_TEST_TABLE), 4);
        clickOnReservation(3);
        clickOnOpenTable();
        verifyErrorMessageWithParam("TableIsOpenReservation", SALE_TEST_TABLE);
        enterReservations();
        clickOnReservation(3);
        clickOnDelete();
    }

    @After
    public void toRestaurantView() {
        backToRestaurantView();
    }
}
