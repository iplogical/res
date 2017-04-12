package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.RecipeAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;

public class RecipeViewImpl extends AbstractModelViewImpl<RecipeAdapter> implements RecipeView {

    public RecipeViewImpl(RecipeAdapter adapter) {
        super(adapter);
    }

    @Override
    public ProductView getOwner() {
        return new ProductViewImpl(new ProductAdapter(adapter.getAdaptee().getOwner()));
    }

    @Override
    public ProductView getComponent() {
        return new ProductViewImpl(new ProductAdapter(adapter.getAdaptee().getComponent()));
    }

    @Override
    public double getQuantity() {
        return adapter.getAdaptee().getQuantityMultiplier();
    }

    @Override
    public boolean isTrivial() {
        Recipe recipe = adapter.getAdaptee();
        return recipe.getOwner().equals(recipe.getComponent());
    }
}
