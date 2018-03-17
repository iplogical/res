package com.inspirationlogical.receipt.corelib.security;

import com.inspirationlogical.receipt.corelib.security.service.SecurityService;

import io.dropwizard.auth.Authorizer;
import org.springframework.beans.factory.annotation.Autowired;

public class BasicAuthorizer implements Authorizer<User> {

    private SecurityService securityService;

    @Autowired
    public BasicAuthorizer(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public boolean authorize(User user, String role) {
        return securityService.authorize(user, role);
    }
}
