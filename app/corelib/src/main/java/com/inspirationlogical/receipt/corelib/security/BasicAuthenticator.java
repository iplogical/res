package com.inspirationlogical.receipt.corelib.security;

import java.util.Optional;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.security.service.SecurityService;

import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {

    private SecurityService securityService;

    @Inject
    public BasicAuthenticator(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials basicCredentials) {
        return Optional.ofNullable(securityService.authenticate(basicCredentials));
    }
}