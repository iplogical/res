package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RestaurantContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    private TableConfigurationController tableConfigurationController;
    private RestaurantController restaurantController;

    public RestaurantContextMenuBuilderDecorator(TableConfigurationController tableConfigurationController,
                                                 RestaurantController restaurantController,
                                                 ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
        this.tableConfigurationController = tableConfigurationController;
        this.restaurantController = restaurantController;
    }

    @Override
    public ContextMenu build() {
        ContextMenu contextMenu = super.build();
        if (restaurantController.isConfigurationMode()) {
            MenuItem addTable = new ContextMenuItemBuilder()
                    .withLabel(WaiterResources.WAITER.getString("ContextMenu.AddTable"))
                    .withClickHandlerPoint2D(tableConfigurationController::showCreateTableForm)
                    .build();
            contextMenu.getItems().add(addTable);
        }
        return contextMenu;
    }
}
