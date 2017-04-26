package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
public class ReservationAdapter extends AbstractAdapter<Reservation> {

    public ReservationAdapter(Reservation adaptee) {
        super(adaptee);
    }

    public static List<ReservationAdapter> getReservationsByDate(LocalDate date) {
        List<Reservation> reservations = GuardedTransaction.runNamedQuery(Reservation.GET_RESERVATIONS_BY_DATE,
                query -> query.setParameter("date", date));
        return reservations.stream().map(ReservationAdapter::new).collect(toList());
    }
}
