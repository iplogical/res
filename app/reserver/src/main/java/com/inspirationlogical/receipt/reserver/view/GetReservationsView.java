package com.inspirationlogical.receipt.reserver.view;

import java.util.List;

import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;

import io.dropwizard.views.View;

public class GetReservationsView extends View {

    private static final String TEMPLATE_NAME = "/view/ftl/reservation.ftl";

    private final List<ReservationViewModel> reservations;

    public GetReservationsView(List<ReservationViewModel> reservations) {
        super(TEMPLATE_NAME);
        this.reservations = reservations;
    }

    public List<ReservationViewModel> getReservations() {
        return reservations;
    }
}
