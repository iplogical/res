package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidRecipe;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "PRODUCT")
@NamedQueries({
    @NamedQuery(name = Product.GET_TEST_PRODUCTS,
            query = "FROM Product p"),
    @NamedQuery(name = Product.GET_PRODUCT_BY_NAME,
            query = "SELECT p FROM Product p WHERE p.longName=:longName"),
    @NamedQuery(name = Product.GET_PRODUCT_BY_ID,
            query = "SELECT p FROM Product p WHERE p.id=:id"),
    @NamedQuery(name = Product.GET_PRODUCT_BY_TYPE,
            query = "SELECT p FROM Product p WHERE p.type=:type"),
    @NamedQuery(name = Product.GET_PRODUCT_BY_STATUS,
            query = "SELECT p FROM Product p WHERE p.status=:status")
})
@AttributeOverride(name = "id", column = @Column(name = "PRODUCT_ID"))
@ValidCategory
//@ValidRecipe
public @Data class Product extends AbstractEntity {

    public static final String GET_TEST_PRODUCTS = "Product.GetTestProducts";
    public static final String GET_PRODUCT_BY_NAME = "Product.GetProductByName";
    public static final String GET_PRODUCT_BY_ID = "Product.GetProductById";
    public static final String GET_PRODUCT_BY_TYPE = "Product.GetProductByType";
    public static final String GET_PRODUCT_BY_STATUS = "Product.GetProductByStatus";
    public static final String DROP_ALL = "Product.DropAll";

    @OneToOne(mappedBy="product", optional = false, fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name="CATEGORY_ID")
    @NotNull
    private ProductCategory category;

    @OneToMany(mappedBy = "owner", fetch=FetchType.LAZY)
    private Collection<Recipe> recipes;

    @OneToMany(mappedBy = "owner", fetch=FetchType.LAZY)
    private Collection<Stock> stocks;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductType type;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductStatus status;

    @NotEmpty
    private String shortName;

    @NotEmpty
    private String longName;

    @Max(999)
    private int rapidCode;

    @Enumerated(EnumType.STRING)
    @NotNull
    private QuantityUnit quantityUnit;

    private double storageMultiplier;

    private int purchasePrice;

    private int salePrice;

    @Max(100)
    private double VATLocal;

    @Max(100)
    private double VATTakeAway;

    private int minimumStock;

    private int stockWindow;

    private int orderNumber;

    @Tolerate
    Product(){}

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
