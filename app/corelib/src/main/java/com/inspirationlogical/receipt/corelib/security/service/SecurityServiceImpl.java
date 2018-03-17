package com.inspirationlogical.receipt.corelib.security.service;

import com.inspirationlogical.receipt.corelib.security.User;
import com.inspirationlogical.receipt.corelib.security.model.adapter.AuthUserAdapter;
import com.inspirationlogical.receipt.corelib.security.model.entity.AuthUser;
import io.dropwizard.auth.basic.BasicCredentials;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SecurityServiceImpl implements SecurityService {

    @Override
    @SneakyThrows
    public User authenticate(BasicCredentials basicCredentials) {
        AuthUserAdapter authUserAdapter = AuthUserAdapter.getUserByUsername(basicCredentials.getUsername());

        if (authUserAdapter != null) {
            AuthUser authUser = authUserAdapter.getAdaptee();

            if (StringUtils.equals(authUser.getPassword(), basicCredentials.getPassword())) {
                return new User(basicCredentials, authUser.getRole());
            }
        }

        return null;
    }

    @Override
    public boolean authorize(User user, String role) {
        return StringUtils.equals(user.getRole(),role);
    }
}
