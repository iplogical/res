package com.inspirationlogical.receipt.dummy;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = false)
@Table(name = "PRODUCT")
@NamedQueries({
    @NamedQuery(name = Product.GET_TEST_PRODUCTS,
            query="FROM Product p")
})
@AttributeOverride(name = "id", column = @Column(name = "PRODUCT_ID"))
@ValidCategory
public @Data class Product extends AbstractEntity {

    public static final String GET_TEST_PRODUCTS = "Product.GetTestProducts";

    @OneToOne(mappedBy="product", optional = false, fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="CATEGORY_ID")
    @NotNull
    private ProductCategory category;

    @NotEmpty
    @Length(max = 20, message = "The field has to be less then 20 characters")
    private String shortName;

    @NotEmpty
    private String LongName;

    @Max(10000)
    private int rapidCode;

    @Enumerated(EnumType.STRING)
    @NotNull
    private QunatityUnit quantityUnit;

    @Enumerated(EnumType.STRING)
    @NotNull
    private EtalonQuantity etalonQuantity;

    private double quantityMultiplier;

    private int purchasePrice;

    private int salePrice;

    @Max(100)
    private int VATLocal;

    @Max(100)
    private int VATTakeAway;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ProductType type;

    private int minimumStore;

    private int storeWindow;
}
