package com.inspirationlogical.receipt.corelib.model.entity;

import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor
public @Data abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_gen")
    @SequenceGenerator(name="id_gen", sequenceName="a_seq", allocationSize=1)
    public int id;

    @Version
    private long version;
}