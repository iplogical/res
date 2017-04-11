package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidReceipts;
import com.inspirationlogical.receipt.corelib.model.annotations.ValidTables;
import com.inspirationlogical.receipt.corelib.model.enums.Orientation;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@javax.persistence.Table(name = "_TABLE")
@NamedQueries({
    @NamedQuery(name = Table.GET_TEST_TABLES,
            query="FROM Table t"),
    @NamedQuery(name = Table.GET_TABLE_BY_NUMBER,
            query="FROM Table t WHERE t.number=:number"),
    @NamedQuery(name = Table.GET_TABLE_ORPHANAGE,
            query="FROM Table t WHERE t.type='ORPHANAGE'")
})
@AttributeOverride(name = "id", column = @Column(name = "TABLE_ID"))
@ValidReceipts
@ValidTables
public @Data class Table extends AbstractEntity {

    public static final String GET_TEST_TABLES = "Table.GetTestTables";
    public static final String GET_TABLE_BY_NUMBER = "Table.GetTableByNumber";
    public static final String GET_TABLE_ORPHANAGE = "Table.GetTableOrphanage";

    @NotNull(message = "A table must belong to a restaurant")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Receipt> receipt;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Reservation> reservation;

    @NotNull(message = "TableType must not be NULL")
    @Enumerated(EnumType.STRING)
    private TableType type;

    @Min(value = 1,message = "Table number must be greater than or equal to 1")
    @Column(unique = true)
    private int number;

    private String name;

    private int coordinateX;

    private int coordinateY;

    private Orientation orientation;

    private int guestNumber;

    private int capacity;

    private String note;

    private boolean visible;

    @Tolerate
    Table(){}

    @Override
    public String toString() {
        return "Table: type=" + type + ", number=" + number + ", name=" + name + ", visibility=" + visible;
    }
}
