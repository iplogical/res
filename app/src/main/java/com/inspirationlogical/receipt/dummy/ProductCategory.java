package com.inspirationlogical.receipt.dummy;

import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"product", "children"})
@Table(name = "PRODUCT_CATEGORY")
@NamedQueries({
    @NamedQuery(name = ProductCategory.GET_TEST_CATEGORIES,
            query="FROM ProductCategory pc")
})
@AttributeOverride(name = "id", column = @Column(name = "CATEGORY_ID"))
public @Data class ProductCategory extends AbstractEntity {

    public static final String GET_TEST_CATEGORIES = "Product.GetTestCategories";

    @NotEmpty
    private String name;

    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.PERSIST)
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent", fetch=FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Collection<ProductCategory> children;

    @Enumerated(EnumType.STRING)
    private ProductCategoryType type;

    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Product product;
}
