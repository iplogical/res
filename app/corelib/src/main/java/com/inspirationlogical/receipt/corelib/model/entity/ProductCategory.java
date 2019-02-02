package com.inspirationlogical.receipt.corelib.model.entity;


import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryFamily;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, exclude = {"product", "children", "parent"})
@Table(name = "PRODUCT_CATEGORY")
@AttributeOverride(name = "id", column = @Column(name = "CATEGORY_ID"))
public @Data
class ProductCategory extends AbstractEntity {

    public static ProductCategoryBuilder builder() {
        return new ProductCategory().toBuilder();
    }

    @Column(name = "NAME", unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ProductCategory parent;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "PRODUCT_CATEGORY_RELATIONS",
            joinColumns = @JoinColumn(name = "parent", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "children", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)))
    private List<ProductCategory> children = new ArrayList<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private List<PriceModifier> priceModifiers = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Column(name = "TYPE")
    @Enumerated(EnumType.STRING)
    private ProductCategoryType type;

    @Column(name = "FAMILY")
    @Enumerated(EnumType.STRING)
    private ProductCategoryFamily family;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @Column(name = "ORDERNUMBER")
    private int orderNumber;

    @Tolerate
    ProductCategory() {
    }

    @Override
    public String toString() {
        return "ProductCategory: name=" + name +
                ", type=" + type.toString() +
                ", parent=" + (parent == null ? "no parent" : parent.getName()) +
                ", product=" + (product == null ? "no product" : product.getLongName());
    }
}
