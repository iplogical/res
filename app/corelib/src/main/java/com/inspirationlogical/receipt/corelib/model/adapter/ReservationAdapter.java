package com.inspirationlogical.receipt.corelib.model.adapter;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;

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

    public static void addReservation(ReservationParams params) {
        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER, query -> query.setParameter("number", params.getTableNumber()));
        Reservation reservation = Reservation.builder()
                .name(params.getName())
                .note(params.getNote())
                .tableNumber(params.getTableNumber())
                .guestCount(params.getGuestCount())
                .date(params.getDate())
                .startTime(LocalDateTime.of(params.getDate(), params.getStartTime()))
                .endTime(LocalDateTime.of(params.getDate(), params.getEndTime()))
                .build();
        if(tables.size() != 0) {
            bindReservationToTable(tables.get(0), reservation);
        } else{
            tables = GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_TYPE,
                    query -> query.setParameter("type", TableType.ORPHANAGE));
            bindReservationToTable(tables.get(0), reservation);
        }
        GuardedTransaction.persist(reservation);
    }

    private static void bindReservationToTable(Table table, Reservation reservation) {
        reservation.setOwner(table);
        table.getReservations().add(reservation);
    }
}
