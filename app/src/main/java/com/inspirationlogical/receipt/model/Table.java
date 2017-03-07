package com.inspirationlogical.receipt.model;

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

import com.inspirationlogical.receipt.model.annotations.ValidReceipts;
import com.inspirationlogical.receipt.model.enums.TableType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@javax.persistence.Table(name = "_TABLE")
@NamedQueries({
    @NamedQuery(name = Table.GET_TEST_TABLES,
            query="FROM Table t")
})
@AttributeOverride(name = "id", column = @Column(name = "TABLE_ID"))
@ValidReceipts
public @Data class Table extends AbstractEntity {

    public static final String GET_TEST_TABLES = "Table.GetTestTables";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant owner;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<Receipt> receipt;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TableType type;

    @Min(1)
    @Column(unique = true)
    private int number;

    private int name;

    private int coordinateX;

    private int coordinateY;

    private int guestNumber;

    private int capacity;

    private String note;

    private boolean visibility;
}
