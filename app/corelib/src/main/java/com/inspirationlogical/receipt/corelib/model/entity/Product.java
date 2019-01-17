package com.inspirationlogical.receipt.corelib.model.entity;

import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;
import java.util.Collection;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "PRODUCT")
@AttributeOverride(name = "id", column = @Column(name = "PRODUCT_ID"))
public @Data
class Product extends AbstractEntity {

    @OneToOne(mappedBy = "product", optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "CATEGORY_ID")
    private ProductCategory category;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Collection<Recipe> recipes;

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private Collection<Stock> stocks;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private String shortName;

    private String longName;

    private int rapidCode;

    @Enumerated(EnumType.STRING)
    private QuantityUnit quantityUnit;

    private double storageMultiplier;

    private int purchasePrice;

    private int salePrice;

    private double VATLocal;

    private double VATTakeAway;

    private int minimumStock;

    private int stockWindow;

    private int orderNumber;

    @Tolerate
    Product() {
    }

    @Override
    public String toString() {
        return "Product: name=" + longName +
                ", type=" + type.toString() +
                ", status=" + status.toString() +
                ", salePrice=" + salePrice +
                ", purchasePrice=" + purchasePrice +
                ", rapidCode=" + rapidCode +
                ", VATLocal=" + VATLocal +
                ", VATTakeAway=" + VATTakeAway;
    }
}
