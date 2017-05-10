package com.inspirationlogical.receipt.corelib.security.model.adapter;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.adapter.AbstractAdapter;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.security.model.entity.AuthUser;

public class AuthUserAdapter extends AbstractAdapter<AuthUser> {

    public static List<AuthUser> getUsers() {
        return GuardedTransaction.runNamedQuery(AuthUser.GET_ALL_USERS);
    }

    public static List<AuthUser> getUsersByUsername(String username) {
        return GuardedTransaction.runNamedQuery(AuthUser.GET_USER_BY_USERNAME, query -> {
            query.setParameter("username", username);
            return query;
        });
    }

    public static AuthUserAdapter getUserByUsername(String username) {
        @SuppressWarnings("unchecked")
        List<AuthUser> users = getUsersByUsername(username);
        if (users.isEmpty()) {
            return null;
        }
        return new AuthUserAdapter(users.get(0));
    }

    public static List<AuthUser> getUserByRole(String role) {
        return GuardedTransaction.runNamedQuery(AuthUser.GET_USER_BY_ROLE, query -> {
            query.setParameter("role", role);
            return query;
        });
    }

    public AuthUserAdapter(AuthUser adaptee) {
        super(adaptee);
    }

    public void setUsername(String username) {
        GuardedTransaction.run(() -> adaptee.setUsername(username));
    }

    public void setPassword(String password) {
        GuardedTransaction.run(() -> adaptee.setPassword(password));
    }

    public void setRole(String role) {
        GuardedTransaction.run(() -> adaptee.setRole(role));
    }

}
