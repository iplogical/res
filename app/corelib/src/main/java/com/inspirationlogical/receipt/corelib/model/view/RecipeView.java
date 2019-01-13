package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import lombok.Getter;
import lombok.ToString;

@ToString
public class RecipeView {

    private Product component;

    @Getter
    private double quantity;
    @Getter
    private boolean isTrivial;

    public RecipeView(Recipe recipe) {
        component = recipe.getComponent();
        quantity = recipe.getQuantityMultiplier();
        isTrivial = recipe.getOwner().equals(recipe.getComponent());
    }

    public ProductView getComponent() {
        return new ProductView(component);
    }
}
