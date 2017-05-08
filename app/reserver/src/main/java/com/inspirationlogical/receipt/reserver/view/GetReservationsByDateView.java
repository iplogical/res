package com.inspirationlogical.receipt.reserver.view;

import java.time.LocalDate;
import java.util.List;

import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;

import io.dropwizard.views.View;

public class GetReservationsByDateView extends View {

    private static final String TEMPLATE_NAME = "/view/ftl/reservationsByDate.ftl";

    private final LocalDate localDate;

    private final List<ReservationViewModel> reservations;

    public GetReservationsByDateView(List<ReservationViewModel> reservations, LocalDate localDate) {
        super(TEMPLATE_NAME);
        this.localDate = localDate;
        this.reservations = reservations;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public List<ReservationViewModel> getReservations() {
        return reservations;
    }
}
