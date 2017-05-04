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
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class ReserverApp extends Application<ReserverConfiguration> {

    private static final String SERVER_MODE = "server";
    private static final String CONFIG_PATH = "/configuration/config.yml";

    private static final String CORSFILTER = "CORS";
    private static final String CORS_SUPPORTED_METHODS = "cors.supportedMethods";
    private static final String CORS_SUPPORTS_CREDENTIALS = "cors.supportsCredentials";
    private static final String CORS_EXPOSED_HEADERS = "cors.exposedHeaders";
    private static final String CORS_SUPPORTED_HEADERS = "cors.supportedHeaders";
    private static final String CORS_ALLOW_ORIGIN = "cors.allowOrigin";

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
        bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());
        bootstrap.addBundle(new AssetsBundle("/assets/", "/assets"));
    }

    @Override
    public void run(ReserverConfiguration configuration,
                    Environment environment) {
        final ReservationHealthCheck healthCheck = new ReservationHealthCheck();
        environment.healthChecks().register("reservation", healthCheck);
        environment.jersey().register(reservationResource);

//        final FilterRegistration.Dynamic filter = environment.servlets().addFilter(CORSFILTER, CORSFilter.class);
//        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
//        filter.setInitParameter(CORS_ALLOW_ORIGIN, "*");
//        filter.setInitParameter(CORS_SUPPORTED_HEADERS, "*");
//        filter.setInitParameter(CORS_SUPPORTS_CREDENTIALS, "true");
//        filter.setInitParameter(CORS_SUPPORTED_METHODS, "*");
//        filter.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());

        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // DO NOT pass a preflight request to down-stream auth filters
        // unauthenticated preflight requests should be permitted by spec
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }

}
