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

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private ProductType type;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "SHORTNAME")
    private String shortName;

    @Column(name = "LONGNAME")
    private String longName;

    @Column(name = "RAPIDCODE")
    private int rapidCode;

    @Column(name = "QUANTITYUNIT")
    @Enumerated(EnumType.STRING)
    private QuantityUnit quantityUnit;

    @Column(name = "STORAGEMULTIPLIER")
    private double storageMultiplier;

    @Column(name = "PURCHASEPRICE")
    private int purchasePrice;

    @Column(name = "SALEPRICE")
    private int salePrice;

    @ManyToOne
    @JoinColumn(name = "VATLOCAL")
    private VAT VATLocal;

    @ManyToOne
    @JoinColumn(name = "VATTAKEAWAY")
    private VAT VATTakeAway;

    @Column(name = "MINIMUMSTOCK")
    private int minimumStock;

    @Column(name = "STOCKWINDOW")
    private int stockWindow;

    @Column(name = "ORDERNUMBER")
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
