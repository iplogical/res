package com.inspirationlogical.receipt.corelib.security;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.security.service.SecurityService;

import io.dropwizard.auth.Authorizer;

public class BasicAuthorizer implements Authorizer<User> {

    private SecurityService securityService;

    @Inject
    public BasicAuthorizer(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public boolean authorize(User user, String role) {
        return securityService.authorize(user, role);
    }
}
