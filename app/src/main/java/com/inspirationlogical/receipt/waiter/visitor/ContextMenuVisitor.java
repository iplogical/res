package com.inspirationlogical.receipt.waiter.visitor;

import com.inspirationlogical.receipt.waiter.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public interface ContextMenuVisitor {
    ContextMenuBuilder visit(VBox table, RestaurantViewState restaurantViewState);
    ContextMenuBuilder visit(AnchorPane restaurant, RestaurantViewState restaurantViewState);
}
