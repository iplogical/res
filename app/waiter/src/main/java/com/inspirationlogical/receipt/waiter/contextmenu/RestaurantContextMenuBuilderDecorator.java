package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import com.inspirationlogical.receipt.waiter.controller.restaurant.TableConfigurationController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RestaurantContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    private RestaurantController restaurantController;

    private TableConfigurationController tableConfigurationController;

    public RestaurantContextMenuBuilderDecorator(RestaurantController restaurantController,
                                                 TableConfigurationController tableConfigurationController,
                                                 ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
        this.restaurantController = restaurantController;
        this.tableConfigurationController = tableConfigurationController;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        RestaurantViewState restaurantViewState = (RestaurantViewState) viewState;
        ContextMenu contextMenu = super.build(viewState);
        if (restaurantViewState.getConfigurable().getValue()) {
            MenuItem addTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.WAITER.getString("ContextMenu.AddTable"))
 //                   .withClickHandlerPoint2D(restaurantController::showCreateTableForm)
                    .withClickHandlerPoint2D(tableConfigurationController::showCreateTableForm)
                    .build();
            contextMenu.getItems().addAll(addTable);
            if (restaurantViewState.getTableType().equals(TableType.NORMAL) && restaurantViewState.hasSelection()) {
                MenuItem mergeTables = new ContextMenuItemBuilder()
                        .withLabel(Resources.WAITER.getString("ContextMenu.MergeTable"))
//                        .withClickHandler(restaurantController::mergeTables)
                        .withClickHandler(tableConfigurationController::mergeTables)
                        .build();
                contextMenu.getItems().addAll(mergeTables);
            }
        }
        return contextMenu;
    }
}
