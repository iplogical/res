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

    private double initialQuantity;

    private double soldQuantity;

    private double purchasedQuantity;

    private double inventoryQuantity;

    private double disposedQuantity;

    private LocalDateTime date;

    @Tolerate
    Stock() {
    }
}
