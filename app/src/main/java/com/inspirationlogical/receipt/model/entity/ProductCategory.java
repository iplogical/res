package com.inspirationlogical.receipt.model.entity;

import java.util.Collection;

import javax.persistence.*;
import javax.persistence.Table;

import lombok.experimental.Tolerate;
import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.model.annotations.ValidParent;
import com.inspirationlogical.receipt.model.annotations.ValidProduct;
import com.inspirationlogical.receipt.model.enums.ProductCategoryType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"product", "children", "parent"})
@Table(name = "PRODUCT_CATEGORY")
@NamedQueries({
    @NamedQuery(name = ProductCategory.GET_ALL_CATEGORIES,
            query="FROM ProductCategory pc")
})
@AttributeOverride(name = "id", column = @Column(name = "CATEGORY_ID"))
@ValidProduct
@ValidParent
public @Data class ProductCategory extends AbstractEntity {

    public static final String GET_ALL_CATEGORIES = "ProductCategory.GetTestCategories";

    @NotEmpty
    private String name;

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ProductCategory parent;

    @OneToMany(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinTable(
            name = "Product_Category_Relations",
            joinColumns = @JoinColumn(name = "parent",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "children",
                    foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    )
    private Collection<ProductCategory> children;

    @OneToMany(mappedBy = "owner", fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private Collection<PriceModifier> priceModifier;

    @OneToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Enumerated(EnumType.STRING)
    private ProductCategoryType type;

    @Tolerate
    ProductCategory(){}
}
