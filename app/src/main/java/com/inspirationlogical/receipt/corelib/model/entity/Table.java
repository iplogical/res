package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidReceipts;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import lombok.*;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@javax.persistence.Table(name = "_TABLE")
@NamedQueries({
    @NamedQuery(name = Table.GET_TEST_TABLES,
            query="FROM Table t"),
    @NamedQuery(name = Table.GET_TABLE_BY_NUMBER,
            query="FROM Table t WHERE t.number=:number")
})
@AttributeOverride(name = "id", column = @Column(name = "TABLE_ID"))
@ValidReceipts
public @Data class Table extends AbstractEntity {

    public static final String GET_TEST_TABLES = "Table.GetTestTables";
    public static final String GET_TABLE_BY_NUMBER = "Table.GetTableByNumber";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Receipt> receipt;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Reservation> reservation;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TableType type;

    @Min(1)
    @Column(unique = true)
    private int number;

    private String name;

    private int coordinateX;

    private int coordinateY;

    private int guestNumber;

    private int capacity;

    private String note;

    private boolean visibility;

    @Tolerate
    Table(){}
}