package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.corelib.service.reservation.ReservationServiceImpl;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.NUMBER_OF_RESERVATIONS;
import static org.junit.Assert.assertEquals;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
public class ReservationAdapterTest extends TestBase {

    private ReservationParams params;

    @Before
    public void setUp() {
        params = ReservationParams.builder()
                .name("TestName1")
                .note("TestNote1")
                .tableNumber(1)
                .guestCount(6)
                .date(LocalDate.now())
                .startTime(LocalTime.now().minusHours(2))
                .endTime(LocalTime.now().plusHours(2))
                .build();
    }

    @Test
    public void testGetReservationsByDate() {
        assertEquals(NUMBER_OF_RESERVATIONS, ReservationServiceImpl.getReservations(LocalDate.now()).size());
    }

    @Test
    public void testAddReservation() {
        ReservationServiceImpl.addReservation(params);
        assertEquals(NUMBER_OF_RESERVATIONS + 1, ReservationServiceImpl.getReservations(LocalDate.now()).size());
    }
}
