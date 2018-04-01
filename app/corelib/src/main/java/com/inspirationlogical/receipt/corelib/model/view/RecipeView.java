package com.inspirationlogical.receipt.corelib.model.view;

public interface RecipeView {

    ProductView getComponent();

    double getQuantity();

    boolean isTrivial();
}
