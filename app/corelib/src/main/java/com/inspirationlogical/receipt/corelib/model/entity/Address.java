package com.inspirationlogical.receipt.corelib.model.entity;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.NotEmpty;

@Builder(toBuilder = true)
@Embeddable
public @Data class Address {

    @NotNull
    @NotEmpty
    private String ZIPCode;

    @NotNull
    @NotEmpty
    private String city;

    @NotNull
    @NotEmpty
    private String street;

    @Tolerate
    public Address() {}
}
