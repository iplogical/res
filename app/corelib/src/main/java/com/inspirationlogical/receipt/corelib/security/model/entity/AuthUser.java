package com.inspirationlogical.receipt.corelib.security.model.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotBlank;

import com.inspirationlogical.receipt.corelib.model.entity.AbstractEntity;
import com.inspirationlogical.receipt.corelib.security.model.annotations.ValidRole;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "AUTHUSER")
@NamedQueries({
    @NamedQuery(name = AuthUser.GET_ALL_USERS,
            query="FROM AuthUser u"),
    @NamedQuery(name = AuthUser.GET_USER_BY_USERNAME,
            query="FROM AuthUser u WHERE u.username=:username"),
    @NamedQuery(name = AuthUser.GET_USER_BY_ROLE,
            query="FROM AuthUser u WHERE u.role=:role"),
})
@AttributeOverride(name = "id", column = @Column(name = "AUTHUSER_ID"))
public @Data class AuthUser extends AbstractEntity {

    public static final String GET_ALL_USERS = "AuthUser.GetAllUsers";
    public static final String GET_USER_BY_USERNAME = "AuthUser.GetUserByUsername";
    public static final String GET_USER_BY_ROLE = "AuthUser.GetUserByRole";

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    @ValidRole
    private String role;

    @Tolerate
    AuthUser(){}

    @Override
    public String toString() {
        return "AuthUser: username=" + username + ", password=" + password + ", role=" + role;
    }
}
