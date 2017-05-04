package com.inspirationlogical.receipt.reserver.application;

import static com.inspirationlogical.receipt.reserver.registry.ReserverRegistry.getInstance;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;

import com.inspirationlogical.receipt.reserver.configuration.ReserverConfiguration;
import com.inspirationlogical.receipt.reserver.healthcheck.ReservationHealthCheck;
import com.inspirationlogical.receipt.reserver.resource.ReservationResource;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class ReserverApp extends Application<ReserverConfiguration> {

    private static final String SERVER_MODE = "server";
    private static final String CONFIG_PATH = "/configuration/config.yml";

    private ReservationResource reservationResource;

    public ReserverApp(ReservationResource reservationResource) {
        this.reservationResource = reservationResource;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            args = new String[2];
            args[0] = SERVER_MODE;
            args[1] = ReserverApp.class.getResource(CONFIG_PATH).getPath();
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

        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter("allowedOrigins", "http://localhost:9000/reservations, http://websocket.dnet.hu:9000/reservation");    // allowed origins comma separated
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
        filter.setInitParameter("allowCredentials", "true");
    }

}
