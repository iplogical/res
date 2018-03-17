package com.inspirationlogical.receipt.corelib.security;

import com.inspirationlogical.receipt.corelib.security.service.SecurityService;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class BasicAuthenticator implements Authenticator<BasicCredentials, User> {

    private SecurityService securityService;

    @Autowired
    public BasicAuthenticator(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials basicCredentials) {
        return Optional.ofNullable(securityService.authenticate(basicCredentials));
    }
}