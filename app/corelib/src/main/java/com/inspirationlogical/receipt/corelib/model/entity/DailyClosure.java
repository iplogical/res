package com.inspirationlogical.receipt.corelib.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static com.inspirationlogical.receipt.corelib.model.entity.DailyClosure.*;

/**
 * Created by TheDagi on 2017. 04. 17..
 */

@Entity
@Builder
@Table(name = "DAILY_CLOSURE")
@EqualsAndHashCode(callSuper = true, exclude = "owner")
@NamedQueries({
        @NamedQuery(name = GET_TEST_DAILY_CLOSURES,
                query = "FROM DailyClosure dc"),
        @NamedQuery(name = GET_LATEST_DAILY_CLOSURE,
                query = "FROM DailyClosure dc ORDER BY dc.closureTime desc"),
        @NamedQuery(name = GET_DAILY_CLOSURE_BEFORE_DATE,
                query = "FROM DailyClosure dc WHERE dc.closureTime < :closureTime ORDER BY dc.closureTime desc"),
        @NamedQuery(name = GET_DAILY_CLOSURE_AFTER_DATE,
                query = "FROM DailyClosure dc WHERE dc.closureTime > :closureTime ORDER BY dc.closureTime asc"),
        @NamedQuery(name = GET_OPEN_DAILY_CLOSURE,
                query = "FROM DailyClosure dc WHERE dc.closureTime IS NULL")
})
@AttributeOverride(name = "id", column = @Column(name = "DAILY_CLOSURE_ID"))
public @Data class DailyClosure extends AbstractEntity {

    public static final String GET_TEST_DAILY_CLOSURES = "Table.GetTestDailyClosures";
    public static final String GET_LATEST_DAILY_CLOSURE = "Table.GetLatestDailyClosure";
    public static final String GET_DAILY_CLOSURE_BEFORE_DATE = "Table.GetDailyClosureBeforeDate";
    public static final String GET_DAILY_CLOSURE_AFTER_DATE = "Table.GetDailyClosureAfterDate";
    public static final String GET_OPEN_DAILY_CLOSURE = "Table.GetOpenDailyClosure";


    @NotNull(message = "A daily closure must belong to a restaurant")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant owner;

    private LocalDateTime closureTime;

    private int sumPurchaseNetPriceCash;
    private int sumPurchaseNetPriceCreditCard;
    private int sumPurchaseNetPriceCoupon;
    private int sumPurchaseNetPriceTotal;

    private int sumPurchaseGrossPriceCash;
    private int sumPurchaseGrossPriceCreditCard;
    private int sumPurchaseGrossPriceCoupon;
    private int sumPurchaseGrossPriceTotal;

    private int sumSaleNetPriceCash;
    private int sumSaleNetPriceCreditCard;
    private int sumSaleNetPriceCoupon;
    private int sumSaleNetPriceTotal;

    private int sumSaleGrossPriceCash;
    private int sumSaleGrossPriceCreditCard;
    private int sumSaleGrossPriceCoupon;
    private int sumSaleGrossPriceTotal;

    private int profit;

    private double markup;

    int receiptAverage;

    int numberOfReceipts;

    double discount;

    @Tolerate
    DailyClosure() {}
}
