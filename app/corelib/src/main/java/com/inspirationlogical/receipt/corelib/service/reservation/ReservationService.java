package com.inspirationlogical.receipt.corelib.service.reservation;

import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    List<Reservation> getReservations();

    List<ReservationView> getReservations(LocalDate date);

    Reservation getReservationById(int id);

    long addReservation(ReservationParams params);

    void updateReservation(int reservationId, ReservationParams params);

    void deleteReservation(int reservationId);
}
