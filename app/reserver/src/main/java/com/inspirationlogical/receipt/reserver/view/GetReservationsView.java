package com.inspirationlogical.receipt.reserver.view;

import io.dropwizard.views.View;

public class GetReservationsView extends View {

    private static final String TEMPLATE_NAME = "/view/ftl/reservations.ftl";

    public GetReservationsView() {
        super(TEMPLATE_NAME);
    }
}
