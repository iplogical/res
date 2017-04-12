package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;

import com.inspirationlogical.receipt.corelib.model.view.RecipeView;

public class RecipeViewModel {
    String component;
    String quantity;
    String unit;

    public RecipeViewModel(RecipeView recipeView) {
        component = recipeView.getComponent().getLongName();
        quantity = valueOf(recipeView.getQuantity());
        unit = recipeView.getComponent().getQuantityUnit().name();
    }
}
