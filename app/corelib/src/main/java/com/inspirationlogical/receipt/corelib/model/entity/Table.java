package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidReceipts;
import com.inspirationlogical.receipt.corelib.model.annotations.ValidTables;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"owner", "consumer", "consumed"})
@javax.persistence.Table(name = "_TABLE")
@NamedQueries({
    @NamedQuery(name = Table.GET_ALL_TABLES,
            query="FROM Table t"),
    @NamedQuery(name = Table.GET_TABLE_BY_NUMBER,
            query="FROM Table t WHERE t.number=:number"),
    @NamedQuery(name = Table.GET_TABLE_BY_TYPE,
            query="FROM Table t WHERE t.type=:type"),
    @NamedQuery(name = Table.GET_TABLE_BY_CONSUMER,
            query="FROM Table t WHERE t.consumer=:consumer"),
    @NamedQuery(name = Table.GET_TABLE_BY_HOST,
            query="FROM Table t WHERE t.host=:host"),
    @NamedQuery(name = Table.GET_FIRST_UNUSED_NUMBER,
            query="FROM Table AS t1 WHERE NOT EXISTS (FROM Table AS t2 WHERE t2.number = t1.number + 1)")
})
@AttributeOverride(name = "id", column = @Column(name = "TABLE_ID"))
@ValidReceipts
@ValidTables
public @Data class Table extends AbstractEntity {

    public static final String GET_ALL_TABLES = "Table.GetAllTables";
    public static final String GET_TABLE_BY_NUMBER = "Table.GetTableByNumber";
    public static final String GET_TABLE_BY_TYPE = "Table.GetTableByType";
    public static final String GET_TABLE_BY_CONSUMER = "Table.GetTableByConsumer";
    public static final String GET_TABLE_BY_HOST = "Table.GetTableByHost";
    public static final String GET_FIRST_UNUSED_NUMBER = "Table.GetFirstUnusedNumber";

    @NotNull(message = "A table must belong to a restaurant")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Receipt> receipts;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Reservation> reservations;

    @NotNull(message = "TableType must not be NULL")
    @Enumerated(EnumType.STRING)
    private TableType type;

    @Min(value = 0,message = "Table number must be greater than or equal to 0")
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "CONSUMER_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Table consumer;

    @OneToMany(mappedBy = "consumer", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Table> consumed;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "HOST_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Table host;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Table> hosted;

    @Tolerate
    Table(){}

    @Override
    public String toString() {
        return "Table: type=" + type + ", number=" + number + ", name=" + name + ", visibility=" + visible;
    }
}
