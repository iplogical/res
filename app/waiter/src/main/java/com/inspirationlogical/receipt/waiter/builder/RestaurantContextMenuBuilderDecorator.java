package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.corelib.frontend.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RestaurantContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    private RestaurantController restaurantController;

    public RestaurantContextMenuBuilderDecorator(RestaurantController restaurantController, ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
        this.restaurantController = restaurantController;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        RestaurantViewState restaurantViewState = (RestaurantViewState) viewState;
        ContextMenu contextMenu = super.build(viewState);
        if (restaurantViewState.getConfigurable().getValue()) {
            MenuItem addTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.WAITER.getString("ContextMenu.AddTable"))
                    .withClickHandlerPoint2D(restaurantController::showCreateTableForm)
                    .build();
            contextMenu.getItems().addAll(addTable);
            if (restaurantViewState.getTableType().equals(TableType.NORMAL) && restaurantViewState.isHasSelection()) {
                MenuItem mergeTables = new ContextMenuItemBuilder()
                        .withLabel(Resources.WAITER.getString("ContextMenu.MergeTable"))
                        .withClickHandler(restaurantController::mergeTables)
                        .build();
                contextMenu.getItems().addAll(mergeTables);
            }
        }
        return contextMenu;
    }
}
