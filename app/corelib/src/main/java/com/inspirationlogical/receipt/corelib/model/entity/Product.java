package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidCategory;
import com.inspirationlogical.receipt.corelib.model.enums.EtalonQuantity;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "PRODUCT")
@NamedQueries({
    @NamedQuery(name = Product.GET_TEST_PRODUCTS,
            query = "FROM Product p"),
    @NamedQuery(name = Product.GET_PRODUCT_BY_NAME,
            query = "SELECT p FROM Product p WHERE p.longName=:longName"),
    @NamedQuery(name = Product.GET_PRODUCT_BY_TYPE,
            query = "SELECT p FROM Product p WHERE p.type=:type")
})
@AttributeOverride(name = "id", column = @Column(name = "PRODUCT_ID"))
@ValidCategory
public @Data class Product extends AbstractEntity {

    public static final String GET_TEST_PRODUCTS = "Product.GetTestProducts";
    public static final String GET_PRODUCT_BY_NAME = "Product.GetProductByName";
    public static final String GET_PRODUCT_BY_TYPE = "Product.GetProductByType";
    public static final String DROP_ALL = "Product.DropAll";

    @OneToOne(mappedBy="product", optional = false, fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name="CATEGORY_ID")
    @NotNull
    private ProductCategory category;

    @OneToMany(mappedBy = "owner", fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Recipe> recipe;

    @OneToMany(mappedBy = "owner", fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<Stock> stock;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductType type;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductStatus status;

    @NotEmpty
    @Length(max = 20, message = "The field has to be less then 20 characters")
    private String shortName;

    @NotEmpty
    private String longName;

    @Max(10000)
    private int rapidCode;

    @Enumerated(EnumType.STRING)
    @NotNull
    private QuantityUnit quantityUnit;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EtalonQuantity etalonQuantity;

    private double quantityMultiplier;

    private int purchasePrice;

    private int salePrice;

    @Max(100)
    private double VATLocal;

    @Max(100)
    private double VATTakeAway;

    private int minimumStore;

    private int storeWindow;

    @Tolerate
    Product(){}

    @Override
    public String toString() {
        return "Product: name=" + longName +
                ", type=" + type.toString() +
                ", status=" + status.toString() +
                ", salePrice=" + salePrice +
                ", purchasePrice=" + purchasePrice +
                ", VATLocal=" + VATLocal +
                ", VATTakeAway=" + VATTakeAway;
    }
}
