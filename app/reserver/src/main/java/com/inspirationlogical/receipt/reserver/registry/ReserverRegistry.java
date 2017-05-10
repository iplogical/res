package com.inspirationlogical.receipt.reserver.registry;

import com.google.inject.TypeLiteral;
import com.inspirationlogical.receipt.corelib.frontend.registry.Registry;
import com.inspirationlogical.receipt.corelib.security.BasicAuthenticator;
import com.inspirationlogical.receipt.corelib.security.BasicAuthorizer;
import com.inspirationlogical.receipt.corelib.security.User;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.Authorizer;
import io.dropwizard.auth.basic.BasicCredentials;

public class ReserverRegistry extends Registry {

    public static <T> T getInstance(Class<T> clazz) {
        return getInjector(ReserverRegistry.class).getInstance(clazz);
    }

    @Override
    protected void configure() {
        super.configure();
        bind(new TypeLiteral<Authenticator<BasicCredentials, User>>(){}).to(BasicAuthenticator.class);
        bind(new TypeLiteral<Authorizer<User>>(){}).to(BasicAuthorizer.class);
    }
}
