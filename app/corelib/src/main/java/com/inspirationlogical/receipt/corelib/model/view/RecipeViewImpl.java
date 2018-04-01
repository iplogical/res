package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RecipeViewImpl implements RecipeView {

    private Product component;
    private double quantity;
    private boolean isTrivial;

    public RecipeViewImpl(Recipe recipe) {
        component = recipe.getComponent();
        quantity = recipe.getQuantityMultiplier();
        isTrivial = recipe.getOwner().equals(recipe.getComponent());
    }

    @Override
    public ProductView getComponent() {
        return new ProductViewImpl(component);
    }
}
