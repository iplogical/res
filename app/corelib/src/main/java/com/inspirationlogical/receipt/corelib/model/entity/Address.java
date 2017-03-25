package com.inspirationlogical.receipt.corelib.model.entity;

import javax.persistence.Embeddable;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Builder
@Embeddable
public @Data class Address {

    private String ZIPCode;

    private String city;

    private String street;

    @Tolerate
    public Address() {}
}
