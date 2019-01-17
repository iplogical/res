package com.inspirationlogical.receipt.corelib.model.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "RECIPE")
@AttributeOverride(name = "id", column = @Column(name = "RECIPE_ID"))
//@ValidRecipe
public @Data class Recipe extends AbstractEntity {

    public static final String GET_TEST_RECIPES = "Recipe.GetTestRecipes";
    public static final String GET_RECIPES_OF_PRODUCT = "Recipe.GetRecipesOfProduct";

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID")
    private Product owner;

    @OneToOne
    private Product component;

    private double quantityMultiplier;

    @Tolerate
    Recipe(){}
}
