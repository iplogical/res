package com.inspirationlogical.receipt.corelib.model.entity;

import java.time.LocalDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
    @NamedQuery(name = Stock.STOCK_GET_ITEMS, query="FROM Stock s"),
    @NamedQuery(name = Stock.STOCK_GET_ITEM_BY_PRODUCT,
            query="FROM Stock s WHERE s.owner=:product ORDER BY date")
})
@AttributeOverride(name = "id", column = @Column(name = "STOCK_ID"))
public @Data class Stock extends AbstractEntity {

    public static final String STOCK_GET_ITEMS = "Stock.GetStockItems";
    public static final String STOCK_GET_ITEM_BY_PRODUCT = "Stock.GetStockItemByProduct";

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @NotNull
    private Product owner;

    private double initialQuantity;

    private double soldQuantity;

    private double purchasedQuantity;

    private double inventoryQuantity;

    private double disposedQuantity;

    private LocalDateTime date;

    @Tolerate
    Stock(){}
}
