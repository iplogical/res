package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;

import com.inspirationlogical.receipt.corelib.model.view.RecipeView;
import lombok.Builder;
import lombok.Data;

@Builder
public @Data class RecipeViewModel {
    String componentLongName;
    String quantity;
    String unit;

    public RecipeViewModel(String componentLongName, String quantity, String unit) {
        this.componentLongName = componentLongName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public RecipeViewModel(RecipeView recipeView) {
        componentLongName = recipeView.getComponent().getLongName();
        quantity = valueOf(recipeView.getQuantity());
        unit = recipeView.getComponent().getQuantityUnit().name();
    }
}
