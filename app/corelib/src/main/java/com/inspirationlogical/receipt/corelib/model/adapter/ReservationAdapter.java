package com.inspirationlogical.receipt.corelib.model.adapter;

import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.inspirationlogical.receipt.corelib.exception.ReservationNotFoundException;
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

    public static List<ReservationAdapter> getReservations() {
        List<Reservation> reservations = GuardedTransaction.runNamedQuery(Reservation.GET_RESERVATIONS);
        return reservations.stream().map(ReservationAdapter::new).collect(toList());
    }

    public static List<ReservationAdapter> getReservationsByDate(LocalDate date) {
        List<Reservation> reservations = GuardedTransaction.runNamedQuery(Reservation.GET_RESERVATIONS_BY_DATE,
                query -> query.setParameter("date", date));
        return reservations.stream().map(ReservationAdapter::new).collect(toList());
    }

    public static ReservationAdapter getReservationById(long id) {
        List<Reservation> reservations = GuardedTransaction.runNamedQuery(Reservation.GET_RESERVATION_BY_ID,
                query -> query.setParameter("id", id).setMaxResults(1));
        return reservations.stream().map(ReservationAdapter::new).findFirst().orElseThrow(ReservationNotFoundException::new);
    }

    public static void addReservation(ReservationParams params) {
        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER, query -> query.setParameter("number", params.getTableNumber()));
        Reservation reservation = Reservation.builder()
                .name(params.getName())
                .note(params.getNote())
                .tableNumber(params.getTableNumber())
                .guestCount(params.getGuestCount())
                .phoneNumber(params.getPhoneNumber())
                .date(params.getDate())
                .startTime(LocalDateTime.of(params.getDate(), params.getStartTime()))
                .endTime(LocalDateTime.of(params.getDate(), params.getEndTime()))
                .build();
        bindReservationToTableOrOrphanage(tables, reservation);
        GuardedTransaction.persist(reservation);
    }

    public void updateReservation(ReservationParams params) {
        List<Table> tables = GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_NUMBER, query -> query.setParameter("number", params.getTableNumber()));
        GuardedTransaction.run(() -> {
            adaptee.setName(params.getName());
            adaptee.setNote(params.getNote());
            adaptee.setTableNumber(params.getTableNumber());
            adaptee.setGuestCount(params.getGuestCount());
            adaptee.setPhoneNumber(params.getPhoneNumber());
            adaptee.setDate(params.getDate());
            adaptee.setStartTime(LocalDateTime.of(params.getDate(), params.getStartTime()));
            adaptee.setEndTime(LocalDateTime.of(params.getDate(), params.getEndTime()));
            adaptee.getOwner().getReservations().remove(adaptee);
            adaptee.setOwner(null);
            bindReservationToTableOrOrphanage(tables, adaptee);
        });
    }

    public void deleteReservation() {
        GuardedTransaction.delete(adaptee, () -> {});
    }

    private static void bindReservationToTableOrOrphanage(List<Table> tables, Reservation reservation) {
        if(tables.size() != 0) {
            bindReservationToTable(tables.get(0), reservation);
        } else{
            tables = GuardedTransaction.runNamedQuery(Table.GET_TABLE_BY_TYPE,
                    query -> query.setParameter("type", TableType.ORPHANAGE));
            bindReservationToTable(tables.get(0), reservation);
        }
    }

    private static void bindReservationToTable(Table table, Reservation reservation) {
        reservation.setOwner(table);
        table.getReservations().add(reservation);
    }
}
