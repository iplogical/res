package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
@Builder
public @Data class ReservationParams {

    private String name;

    private String note;

    private int tableNumber;

    private int guestCount;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;
}
