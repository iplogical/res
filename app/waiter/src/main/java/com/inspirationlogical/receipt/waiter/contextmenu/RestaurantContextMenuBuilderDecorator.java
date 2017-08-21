package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RestaurantContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    private TableConfigurationController tableConfigurationController;

    public RestaurantContextMenuBuilderDecorator(TableConfigurationController tableConfigurationController,
                                                 ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
        this.tableConfigurationController = tableConfigurationController;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        RestaurantViewState restaurantViewState = (RestaurantViewState) viewState;
        ContextMenu contextMenu = super.build(viewState);
        if (restaurantViewState.getConfigurable().getValue()) {
            MenuItem addTable = new ContextMenuItemBuilder()
                    .withLabel(WaiterResources.WAITER.getString("ContextMenu.AddTable"))
                    .withClickHandlerPoint2D(tableConfigurationController::showCreateTableForm)
                    .build();
            contextMenu.getItems().add(addTable);
            if (restaurantViewState.getTableType().equals(TableType.NORMAL) && tableConfigurationController.hasSelection()) {
                MenuItem mergeTables = buildMenuItem("ContextMenu.MergeTable", tableConfigurationController::mergeTables);
                MenuItem exchangeTables = buildMenuItem("ContextMenu.ExchangeTable",tableConfigurationController::exchangeTables);
                contextMenu.getItems().addAll(mergeTables, exchangeTables);
            }
        }
        return contextMenu;
    }
}
