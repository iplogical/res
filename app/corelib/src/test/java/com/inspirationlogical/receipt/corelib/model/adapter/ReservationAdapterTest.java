package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.waiter.controller.ReservationController;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_RESERVATIONS;
import static org.junit.Assert.assertEquals;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
public class ReservationAdapterTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testGetReservationsByDate() {
        assertEquals(NUMBER_OF_RESERVATIONS, ReservationAdapter.getReservationsByDate(LocalDate.now()).size());
    }
}
