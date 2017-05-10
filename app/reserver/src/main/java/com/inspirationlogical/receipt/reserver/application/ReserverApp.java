package com.inspirationlogical.receipt.reserver.application;

import static com.inspirationlogical.receipt.reserver.registry.ReserverRegistry.getInstance;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.security.User;
import com.inspirationlogical.receipt.corelib.utility.LogConfiguration;
import com.inspirationlogical.receipt.reserver.configuration.ReserverConfiguration;
import com.inspirationlogical.receipt.reserver.healthcheck.ReservationHealthCheck;
import com.inspirationlogical.receipt.reserver.resource.ReservationResource;
import com.inspirationlogical.receipt.reserver.resource.SecurityResource;

import ch.qos.logback.classic.Level;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;

public class ReserverApp extends Application<ReserverConfiguration> {

    private static final String SERVER_MODE = "server";
    private static final String CONFIG_PATH = "/configuration/config.yml";

    private Authenticator<BasicCredentials, User> authenticator;
    private Authorizer<User> authorizer;
    private SecurityResource securityResource;
    private ReservationResource reservationResource;

    @Inject
    private ReserverApp(Authenticator<BasicCredentials, User> authenticator, Authorizer<User> authorizer,
                        SecurityResource securityResource, ReservationResource reservationResource) {
        this.authenticator = authenticator;
        this.authorizer = authorizer;
        this.securityResource = securityResource;
        this.reservationResource = reservationResource;
    }

    public static void main(String[] args) throws Exception {

        LogConfiguration.setLoggingLevel(Level.WARN);

        if (args.length == 0) {
            args = new String[2];
            args[0] = SERVER_MODE;
            args[1] = CONFIG_PATH;
        }

        getInstance(ReserverApp.class).run(args);
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
    public void run(ReserverConfiguration configuration, Environment environment) {
        final ReservationHealthCheck healthCheck = new ReservationHealthCheck();
        environment.healthChecks().register("reservation", healthCheck);
        environment.jersey().register(securityResource);
        environment.jersey().register(reservationResource);

        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());

        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                .setAuthenticator(authenticator)
                .setAuthorizer(authorizer)
                .setRealm("SUPER SECRET STUFF")
                .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }
}
