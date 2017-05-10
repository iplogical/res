package com.inspirationlogical.receipt.reserver.view;

import io.dropwizard.views.View;

public class LogoutView extends View {

    private static final String TEMPLATE_NAME = "/view/ftl/logout.ftl";

    public LogoutView() {
        super(TEMPLATE_NAME);
    }
}