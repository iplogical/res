package com.inspirationlogical.receipt.corelib.exception;

public class ReservationNotFoundException extends EntityNotFoundException {

    public ReservationNotFoundException() {
        super("Reservation");
    }
}
