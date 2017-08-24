package com.inspirationlogical.receipt.corelib.model.entity;


import java.util.*;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidCategory;
import com.inspirationlogical.receipt.corelib.model.annotations.ValidParent;
import com.inspirationlogical.receipt.corelib.model.annotations.ValidProduct;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, exclude = {"product", "children", "parent"})
@Table(name = "PRODUCT_CATEGORY")
@NamedQueries({
    @NamedQuery(name = ProductCategory.GET_ALL_CATEGORIES,
            query="FROM ProductCategory pc"),
    @NamedQuery(name = ProductCategory.GET_CATEGORY_BY_TYPE,
            query="SELECT pc FROM ProductCategory pc WHERE pc.type=:type"),
    @NamedQuery(name = ProductCategory.GET_CATEGORY_BY_NAME,
            query="SELECT pc FROM ProductCategory pc WHERE pc.name=:name"),
    @NamedQuery(name = ProductCategory.GET_CHILD_CATEGORIES,
            query="SELECT pc FROM ProductCategory pc WHERE pc.parent.id=:parent_id")
})
@AttributeOverride(name = "id", column = @Column(name = "CATEGORY_ID"))
@ValidProduct
@ValidParent
@ValidCategory
public @Data class ProductCategory extends AbstractEntity {

    public static final String GET_ALL_CATEGORIES = "ProductCategory.GetAllCategories";
    public static final String GET_CATEGORY_BY_TYPE = "ProductCategory.GetCategoryByType";
    public static final String GET_CATEGORY_BY_NAME = "ProductCategory.GetCategoryByName";
    public static final String GET_CHILD_CATEGORIES = "ProductCategory.GetChildCategories";

    public static ProductCategoryBuilder builder() {
        return new ProductCategory().toBuilder();
    }

    @NotEmpty
    @Column(unique = true)
    private String name;

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ProductCategory parent;

    @OneToMany(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinTable(
            name = "PRODUCT_CATEGORY_RELATIONS",
            joinColumns = @JoinColumn(name = "parent", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "children", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)))
    private Set<ProductCategory> children = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    private List<PriceModifier> priceModifiers = new ArrayList<>();

    @OneToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @Enumerated(EnumType.STRING)
    private ProductCategoryType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private int orderNumber;
    @Tolerate
    ProductCategory(){}

    @Override
    public String toString() {
        return "ProductCategory: name=" + name +
                ", type=" + type.toString() +
                ", parent=" + (parent == null ? "no parent" : parent.getName()) +
                ", product=" + (product == null ? "no product" : product.getLongName());
    }
}
