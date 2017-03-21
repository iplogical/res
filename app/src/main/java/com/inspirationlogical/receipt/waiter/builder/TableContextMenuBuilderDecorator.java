package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.TableController;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class TableContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    private RestaurantController restaurantController;
    private TableController tableController;

    public TableContextMenuBuilderDecorator(RestaurantController restaurantController, TableController tableController, ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
        this.restaurantController = restaurantController;
        this.tableController = tableController;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        RestaurantViewState restaurantViewState = (RestaurantViewState) viewState;
        ContextMenu contextMenu = super.build(viewState);
        if (restaurantViewState.isConfigurationEnabled()) {
            MenuItem editTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.UI.getString("contextMenu.editTable"))
                    .withClickHandlerControl(restaurantController::showEditTableForm)
                    .build();
            MenuItem deleteTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.UI.getString("contextMenu.deleteTable"))
                    .withClickHandlerControl(restaurantController::deleteTable)
                    .build();
            MenuItem splitTables = new ContextMenuItemBuilder()
                    .withLabel(Resources.UI.getString("contextMenu.splitTables"))
                    .build();
            contextMenu.getItems().addAll(editTable, deleteTable, splitTables);
        } else {
            MenuItem rename = new ContextMenuItemBuilder()
                    .withLabel(Resources.UI.getString("contextMenu.configureTable"))
                    .withClickHandlerControl(tableController::showConfigureTableForm)
                    .build();
            contextMenu.getItems().addAll(rename);
        }
        return contextMenu;
    }
}
