package com.inspirationlogical.receipt.corelib.service.reservation;

import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    List<Reservation> getReservations();

    List<Reservation> getReservationsByDate(LocalDate date);

    Reservation getReservationById(long id);

    long addReservation(ReservationParams params);

    void updateReservation(long reservationId, ReservationParams params);

    void deleteReservation(long reservationId);
}
