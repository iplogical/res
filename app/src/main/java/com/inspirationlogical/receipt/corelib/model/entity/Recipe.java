package com.inspirationlogical.receipt.corelib.model.entity;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.inspirationlogical.receipt.corelib.model.enums.QunatityUnit;

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
public @Data class Recipe extends AbstractEntity {

    public static final String GET_TEST_RECIPES = "Recipe.GetTestRecipes";

    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST ,CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID")
    @NotNull
    private Product owner;

    @OneToOne
    @NotNull
    private Product element;

    @Enumerated(EnumType.STRING)
    @NotNull
    private QunatityUnit quantityUnit;

    private double quantityMultiplier;

    @Tolerate
    Recipe(){}
}
