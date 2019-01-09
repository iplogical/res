package com.inspirationlogical.receipt.corelib.service.reservation;

import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.ReservationViewImpl;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.corelib.repository.ReservationRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Override
    public List<Reservation> getReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations;
    }

    @Override
    public List<ReservationView> getReservations(LocalDate date) {
        List<Reservation> reservations = reservationRepository.findAllByDate(date);
        return reservations.stream().map(ReservationViewImpl::new).collect(toList());
    }

    @Override
    public Reservation getReservationById(long id) {
        return reservationRepository.getOne(id);
    }

    @Override
    public long addReservation(ReservationParams params) {
        Table table = tableRepository.findByNumber(params.getTableNumber());
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
        bindReservationToTableOrOrphanage(table, reservation);
        reservationRepository.save(reservation);
        return reservation.getId();
    }

    @Override
    public void updateReservation(long reservationId, ReservationParams params) {
        Reservation reservation = reservationRepository.getOne(reservationId);
        Table table = tableRepository.findByNumber(params.getTableNumber());
        reservation.setName(params.getName());
        reservation.setNote(params.getNote());
        reservation.setTableNumber(params.getTableNumber());
        reservation.setGuestCount(params.getGuestCount());
        reservation.setPhoneNumber(params.getPhoneNumber());
        reservation.setDate(params.getDate());
        reservation.setStartTime(LocalDateTime.of(params.getDate(), params.getStartTime()));
        reservation.setEndTime(LocalDateTime.of(params.getDate(), params.getEndTime()));
        reservation.getOwner().getReservations().remove(reservation);
        reservation.setOwner(null);
        bindReservationToTableOrOrphanage(table, reservation);
        reservationRepository.save(reservation);
    }

    private void bindReservationToTableOrOrphanage(Table table, Reservation reservation) {
        if(table != null) {
            bindReservationToTable(table, reservation);
        } else{
            List<Table> tables = tableRepository.findAllByType(TableType.ORPHANAGE);
            bindReservationToTable(tables.get(0), reservation);
        }
    }

    private void bindReservationToTable(Table table, Reservation reservation) {
        reservation.setOwner(table);
        table.getReservations().add(reservation);
    }

    @Override
    public void deleteReservation(long reservationId) {
        Reservation reservation = reservationRepository.getOne(reservationId);
        reservation.getOwner().getReservations().remove(reservation);
        reservation.setOwner(null);
        reservationRepository.delete(reservation);
    }
}
