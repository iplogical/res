package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Product;

public interface RecipeView {

    Product getOwner();

    Product getComponent();

    double getQuantity();
}
