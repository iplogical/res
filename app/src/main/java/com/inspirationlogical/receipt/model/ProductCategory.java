package com.inspirationlogical.receipt.model;

import java.util.Collection;

import javax.persistence.*;
import javax.persistence.Table;

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
    @NamedQuery(name = ProductCategory.GET_TEST_CATEGORIES,
            query="FROM ProductCategory pc")
})
@AttributeOverride(name = "id", column = @Column(name = "CATEGORY_ID"))
@ValidProduct
@ValidParent
public @Data class ProductCategory extends AbstractEntity {

    public static final String GET_TEST_CATEGORIES = "ProductCategory.GetTestCategories";

    @NotEmpty
    private String name;

    @ManyToOne(fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<ProductCategory> children;

    @OneToMany(mappedBy = "owner", fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private Collection<PriceModifier> priceModifier;

    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Product product;

    @Enumerated(EnumType.STRING)
    private ProductCategoryType type;

}
