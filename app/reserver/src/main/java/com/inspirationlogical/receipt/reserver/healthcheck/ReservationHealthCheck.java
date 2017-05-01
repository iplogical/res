package com.inspirationlogical.receipt.reserver.healthcheck;

import com.codahale.metrics.health.HealthCheck;

public class ReservationHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
