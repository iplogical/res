package com.inspirationlogical.receipt.corelib.security;

import io.dropwizard.auth.PrincipalImpl;
import io.dropwizard.auth.basic.BasicCredentials;
import lombok.Getter;
import lombok.Setter;

public class User extends PrincipalImpl {

    private @Getter @Setter String password;
    private @Getter @Setter String role;

    public User(BasicCredentials basicCredentials, String role) {
        super(basicCredentials.getUsername());
        this.password = basicCredentials.getPassword();
        this.role = role;
    }
}
