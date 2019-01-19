package com.inspirationlogical.receipt.corelib.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "STOCK")
@AttributeOverride(name = "id", column = @Column(name = "STOCK_ID"))
public @Data
class Stock extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product owner;

    @Column(name = "INITIALQUANTITY")
    private double initialQuantity;

    @Column(name = "SOLDQUANTITY")
    private double soldQuantity;

    @Column(name = "PURCHASEDQUANTITY")
    private double purchasedQuantity;

    @Column(name = "INVENTORYQUANTITY")
    private double inventoryQuantity;

    @Column(name = "DISPOSEDQUANTITY")
    private double disposedQuantity;

    @Column(name = "DATE")
    private LocalDateTime date;

    @Tolerate
    Stock() {
    }
}
