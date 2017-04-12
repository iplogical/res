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
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.annotations.ValidRecipe;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;

@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@Table(name = "RECIPE")
@NamedQueries({
    @NamedQuery(name = Recipe.GET_TEST_RECIPES,
            query="FROM Recipe r")
})
@AttributeOverride(name = "id", column = @Column(name = "RECIPE_ID"))
@ValidRecipe
public @Data class Recipe extends AbstractEntity {

    public static final String GET_TEST_RECIPES = "Recipe.GetTestRecipes";

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID")
    @NotNull
    private Product owner;

    @OneToOne
    @NotNull
    private Product component;

    private double quantityMultiplier;

    @Tolerate
    Recipe(){}
}
