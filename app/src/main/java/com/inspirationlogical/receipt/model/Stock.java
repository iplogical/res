package com.inspirationlogical.receipt.model;

import java.util.Calendar;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PRODUCT_ID")
    @NotNull
    private Product owner;

    double soldQuantity;

    double startingStock;

    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date;
}
