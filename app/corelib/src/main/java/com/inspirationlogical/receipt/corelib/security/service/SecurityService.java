package com.inspirationlogical.receipt.corelib.security.service;

import com.inspirationlogical.receipt.corelib.security.User;

import io.dropwizard.auth.basic.BasicCredentials;

public interface SecurityService {

    User authenticate(BasicCredentials basicCredentials);

    boolean authorize(User user, String role);
}
