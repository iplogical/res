package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Calendar;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "STOCK")
@NamedQueries({
    @NamedQuery(name = Stock.GET_TEST_STOCKS,
            query="FROM Stock s")
})
@AttributeOverride(name = "id", column = @Column(name = "STOCK_ID"))
public @Data class Stock extends AbstractEntity {

    public static final String GET_TEST_STOCKS = "Stock.GetTestStocks";

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotNull
    private Product owner;

    double soldQuantity;

    double startingStock;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date;

    @Tolerate
    Stock(){}
}
