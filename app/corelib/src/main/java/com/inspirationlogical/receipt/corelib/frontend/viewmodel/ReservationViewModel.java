package com.inspirationlogical.receipt.corelib.frontend.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.ReservationView;

import lombok.Data;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
public @Data class ReservationViewModel {
    private String name;

    private String tableNumber;

    private String guestCount;

    private String note;

    private String phoneNumber;

    private String date;

    private String startTime;

    private String endTime;

    private long reservationId;

    public ReservationViewModel(ReservationView reservationView) {
        this.name = reservationView.getName();
        this.tableNumber = reservationView.getTableNumber();
        this.guestCount = reservationView.getGuestCount();
        this.note = reservationView.getNote();
        this.phoneNumber = reservationView.getPhoneNumber();
        this.date = reservationView.getDate();
        this.startTime = reservationView.getStartTime();
        this.endTime = reservationView.getEndTime();
        this.reservationId = reservationView.getId();
    }

    public int getTableNumberAsInt() {
        return Integer.valueOf(tableNumber);
    }

    public int getGuestCountAsInt() {
        return Integer.valueOf(guestCount);
    }
}
