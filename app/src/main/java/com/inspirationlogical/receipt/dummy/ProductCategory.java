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

@Entity
@Table(name = "PRODUCT_CATEGORY")
@NamedQueries({
    @NamedQuery(name = ProductCategory.GET_TEST_CATEGORIES,
            query="FROM ProductCategory pc")
})
@AttributeOverride(name = "id", column = @Column(name = "CATEGORY_ID"))
public class ProductCategory extends AbstractEntity {

    public static final String GET_TEST_CATEGORIES = "Product.GetTestCategories";

    @NotEmpty
    private String name;

    @ManyToOne
    private ProductCategory parent;

    @OneToMany(mappedBy="parent")
    private Collection<ProductCategory> children;

    @Enumerated(EnumType.STRING)
    private ProductCategoryType type;

    @OneToOne(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Product product;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductCategory getParent() {
        return parent;
    }

    public void setParent(ProductCategory parent) {
        this.parent = parent;
    }

    public Collection<ProductCategory> getChildren() {
        return children;
    }

    public void setChildren(Collection<ProductCategory> children) {
        this.children = children;
    }

    public ProductCategoryType getType() {
        return type;
    }

    public void setType(ProductCategoryType type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
