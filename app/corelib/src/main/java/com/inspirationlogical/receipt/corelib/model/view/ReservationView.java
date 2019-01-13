package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReservationView {

    private long id;
    private String name;
    private String tableNumber;
    private String guestCount;
    private String note;
    private String phoneNumber;
    private String date;
    private String startTime;
    private String endTime;

    public ReservationView(Reservation reservation) {
        id = reservation.getId();
        name = reservation.getName();
        tableNumber = String.valueOf(reservation.getTableNumber());
        guestCount = String.valueOf(reservation.getGuestCount());
        note = reservation.getNote();
        phoneNumber = reservation.getPhoneNumber();
        date = reservation.getDate().toString();
        startTime = reservation.getStartTime().toLocalTime().toString();
        endTime = reservation.getEndTime().toLocalTime().toString();
    }
}
