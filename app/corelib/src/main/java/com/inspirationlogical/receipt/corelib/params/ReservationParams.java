package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public @Data
class ReservationParams {

    private int reservationId;

    private String name;

    private String note;

    private String phoneNumber;

    private int tableNumber;

    private int guestCount;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    public static final class ReservationParamsBuilder {
    }
}
