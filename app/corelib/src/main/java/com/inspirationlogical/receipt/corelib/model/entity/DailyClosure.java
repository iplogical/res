package com.inspirationlogical.receipt.corelib.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static com.inspirationlogical.receipt.corelib.model.entity.DailyClosure.GET_LATEST_DAILY_CLOSURE;
import static com.inspirationlogical.receipt.corelib.model.entity.DailyClosure.GET_TEST_DAILY_CLOSURES;

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
                query = "FROM DailyClosure dc ORDER BY dc.closureTime desc")
})
@AttributeOverride(name = "id", column = @Column(name = "DAILY_CLOSURE_ID"))
public @Data class DailyClosure extends AbstractEntity {

    public static final String GET_TEST_DAILY_CLOSURES = "Table.GetTestDailyClosures";
    public static final String GET_LATEST_DAILY_CLOSURE = "Table.GetLatestDailyClosure";

    @NotNull(message = "A daily closure must belong to a restaurant")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "RESTAURANT_ID")
    private Restaurant owner;

    private LocalDateTime closureTime;
    
    private int grossCommerceCash;

    private int grossCommerceCreditCard;

    private int grossCommerceCoupon;

    private int grossCommerceSum;

    private int netCommerceCash;

    private int netCommerceCreditCard;

    private int netCommerceCoupon;

    private int netCommerceSum;

    private int profitBeforeVAT;

    private int profitAfterVAT;

    private double markup;
    
    private double frontMarkup;
    
    int tableAverage;

    int numberOfReceipts;

    double discount;

    @Tolerate
    DailyClosure() {}
}
