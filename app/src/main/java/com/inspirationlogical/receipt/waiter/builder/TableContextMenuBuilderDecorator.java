package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class TableContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    private RestaurantController restaurantController;

    public TableContextMenuBuilderDecorator(RestaurantController restaurantController, ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
        this.restaurantController = restaurantController;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        RestaurantViewState restaurantViewState = (RestaurantViewState) viewState;
        ContextMenu contextMenu = super.build(viewState);
        if (restaurantViewState.isConfigurationEnabled()) {
            MenuItem menuItem = new ContextMenuItemBuilder()
                    .withLabel("Asztal szerkesztése")
                    .withClickHandler1(restaurantController::showEditTableForm)
                    .build();
            contextMenu.getItems().add(contextMenu.getItems().size(), menuItem);
        }
        return contextMenu;
    }
}
