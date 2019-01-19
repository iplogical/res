package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@javax.persistence.Table(name = "_TABLE")
@AttributeOverride(name = "id", column = @Column(name = "TABLE_ID"))
public @Data
class Table extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private Collection<Receipt> receipts = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private Collection<Reservation> reservations = new ArrayList<>();

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private TableType type;

    @Column(name = "NUMBER", unique = true)
    private int number;

    @Column(name = "NAME")
    private String name;

    @Column(name = "COORDINATEX")
    private int coordinateX;

    @Column(name = "COORDINATEY")
    private int coordinateY;

    @Column(name = "DIMENSIONX")
    private int dimensionX;

    @Column(name = "DIMENSIONY")
    private int dimensionY;

    @Column(name = "GUESTCOUNT")
    private int guestCount;

    @Column(name = "CAPACITY")
    private int capacity;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "VISIBLE")
    private boolean visible;

    @Tolerate
    Table() {
    }

    @Override
    public String toString() {
        return "Table: type=" + type + ", number=" + number + ", name=" + name + ", visibility=" + visible;
    }
}
