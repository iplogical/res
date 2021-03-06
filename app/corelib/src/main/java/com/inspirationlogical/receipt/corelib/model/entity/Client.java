package com.inspirationlogical.receipt.corelib.model.entity;

import javax.persistence.Embeddable;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

@Builder
@Embeddable
public @Data class Client {

    private String name;

    private String address;

    private String TAXNumber;

    @Tolerate
    public Client() {}
}
