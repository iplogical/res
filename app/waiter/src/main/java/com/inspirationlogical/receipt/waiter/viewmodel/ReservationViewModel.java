package com.inspirationlogical.receipt.waiter.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
public @Data class ReservationViewModel {
    private String name;

    private String tableNumber;

    private String guestCount;

    private String note;

    private String date;

    private String startTime;

    private String endTime;

    public ReservationViewModel(ReservationView reservationView) {
        this.name = reservationView.getName();
        this.tableNumber = reservationView.getTableNumber();
        this.guestCount = reservationView.getGuestCount();
        this.note = reservationView.getNote();
        this.date = reservationView.getDate();
        this.startTime = reservationView.getStartTime();
        this.endTime = reservationView.getEndTime();
    }

    public int getTableNumberAsInt() {
        return Integer.valueOf(tableNumber);
    }

    public int getGuestCountAsInt() {
        return Integer.valueOf(guestCount);
    }
}
