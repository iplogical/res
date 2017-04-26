package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.waiter.controller.ReservationController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RESERVATIONS;
import static org.junit.Assert.assertEquals;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
public class ReservationAdapterTest {

    private ReservationParams params;
    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

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
        assertEquals(NUMBER_OF_RESERVATIONS, ReservationAdapter.getReservationsByDate(LocalDate.now()).size());
    }

    @Test
    public void testAddReservation() {
        ReservationAdapter.addReservation(params);
        assertEquals(NUMBER_OF_RESERVATIONS + 1, ReservationAdapter.getReservationsByDate(LocalDate.now()).size());
    }
}
