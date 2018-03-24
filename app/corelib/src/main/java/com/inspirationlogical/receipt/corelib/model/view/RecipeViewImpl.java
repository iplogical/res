package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RecipeViewImpl implements RecipeView {

    private ProductView owner;
    private ProductView component;
    private double quantity;
    private boolean isTrivial;

    public RecipeViewImpl(Recipe recipe) {
        owner = new ProductViewImpl(recipe.getOwner());
        component = new ProductViewImpl(recipe.getComponent());
        quantity = recipe.getQuantityMultiplier();
        isTrivial = recipe.getOwner().equals(recipe.getComponent());
    }

//    @Override
//    public ProductView getOwner() {
//        return new ProductViewImpl(new ProductAdapter(adapter.getAdaptee().getOwner()));
//    }
//
//    @Override
//    public ProductView getComponent() {
//        return new ProductViewImpl(new ProductAdapter(adapter.getAdaptee().getComponent()));
//    }
//
//    @Override
//    public double getQuantity() {
//        return adapter.getAdaptee().getQuantityMultiplier();
//    }
//
//    @Override
//    public boolean isTrivial() {
//        Recipe recipe = adapter.getAdaptee();
//        return recipe.getOwner().equals(recipe.getComponent());
//    }
}
