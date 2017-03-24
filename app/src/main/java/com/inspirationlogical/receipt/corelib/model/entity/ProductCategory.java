package com.inspirationlogical.receipt.corelib.model.entity;

import java.util.Collection;

import javax.persistence.*;
import javax.persistence.Table;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidCategory;
import lombok.experimental.Tolerate;
import org.apache.batik.css.engine.value.css2.SrcManager;
import org.hibernate.validator.constraints.NotEmpty;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidParent;
import com.inspirationlogical.receipt.corelib.model.annotations.ValidProduct;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"product", "children", "parent"})
@Table(name = "PRODUCT_CATEGORY")
@NamedQueries({
    @NamedQuery(name = ProductCategory.GET_ALL_CATEGORIES,
            query="FROM ProductCategory pc"),
    @NamedQuery(name = ProductCategory.GET_CATEGORY_BY_TYPE,
            query="SELECT pc FROM ProductCategory pc WHERE pc.type=:type")
})
@AttributeOverride(name = "id", column = @Column(name = "CATEGORY_ID"))
@ValidProduct
@ValidParent
@ValidCategory
public @Data class ProductCategory extends AbstractEntity {

    public static final String GET_ALL_CATEGORIES = "ProductCategory.GetTestCategories";
    public static final String GET_CATEGORY_BY_TYPE = "ProductCategory.GetCategoryByType";

    @NotEmpty
    @Column(unique = true)
    private String name;

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ProductCategory parent;

    @OneToMany(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinTable(
            name = "PRODUCT_CATEGORY_RELATIONS",
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

    @Override
    public String toString() {
//        return "ProductCategory: name=" + name +
//                ", type=" + type.toString() +
//                ", parent=" + parent == null ? "no parent" : parent.getName() +
//                ", product=" + product == null ? "no product" : product.getLongName();
        return "ProductCategory: name=" + name +
                ", type=" + type.toString() +
                ", parent=" + (parent == null ? "no parent" : parent.getName()) +
                ", product=" + (product == null ? "no product" : product.getLongName());

    }
}
