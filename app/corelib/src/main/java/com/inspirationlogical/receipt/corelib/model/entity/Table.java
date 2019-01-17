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

    @Enumerated(EnumType.STRING)
    private TableType type;

    @Column(unique = true)
    private int number;

    private String name;

    private int coordinateX;

    private int coordinateY;

    private int dimensionX;

    private int dimensionY;

    private int guestCount;

    private int capacity;

    private String note;

    private boolean visible;

    @Tolerate
    Table() {
    }

    @Override
    public String toString() {
        return "Table: type=" + type + ", number=" + number + ", name=" + name + ", visibility=" + visible;
    }
}
