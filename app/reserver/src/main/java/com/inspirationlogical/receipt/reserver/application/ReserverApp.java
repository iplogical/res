package com.inspirationlogical.receipt.reserver.application;

import static com.inspirationlogical.receipt.reserver.registry.ReserverRegistry.getInstance;

import com.inspirationlogical.receipt.reserver.configuration.ReserverConfiguration;
import com.inspirationlogical.receipt.reserver.healthcheck.ReservationHealthCheck;
import com.inspirationlogical.receipt.reserver.resource.ReservationResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class ReserverApp extends Application<ReserverConfiguration> {

    private static final String SERVER_MODE = "server";
    private static final String CONFIG_PATH = "reserver/src/main/resources/configuration/config.yml";

    private ReservationResource reservationResource;

    public ReserverApp(ReservationResource reservationResource) {
        this.reservationResource = reservationResource;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            args = new String[2];
            args[0] = SERVER_MODE;
            args[1] = CONFIG_PATH;
        }
        new ReserverApp(getInstance(ReservationResource.class)).run(args);
    }

    @Override
    public String getName() {
        return "Reserver";
    }

    @Override
    public void initialize(Bootstrap<ReserverConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle<>());
    }

    @Override
    public void run(ReserverConfiguration configuration,
                    Environment environment) {
        final ReservationHealthCheck healthCheck = new ReservationHealthCheck();
        environment.healthChecks().register("reservation", healthCheck);
        environment.jersey().register(reservationResource);
    }

}
