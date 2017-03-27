package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.TableController;
import com.inspirationlogical.receipt.waiter.viewstate.TableViewState;
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
        TableViewState tableViewState = (TableViewState) viewState;
        ContextMenu contextMenu = super.build(viewState);
        if (tableViewState.isConfigurable()) {
            MenuItem editTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.UI.getString("ContextMenu.editTable"))
                    .withClickHandlerControl(restaurantController::showEditTableForm)
                    .build();
            MenuItem deleteTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.UI.getString("ContextMenu.deleteTable"))
                    .withClickHandlerControl(restaurantController::deleteTable)
                    .build();
            MenuItem rotateTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.UI.getString("ContextMenu.rotateTable"))
                    .withClickHandlerControl(restaurantController::rotateTable)
                    .build();
            contextMenu.getItems().addAll(editTable, deleteTable, rotateTable);
        } else {
            MenuItem rename = new ContextMenuItemBuilder()
                    .withLabel(Resources.UI.getString("ContextMenu.setTable"))
                    .withClickHandlerControl(tableController::showTableSettingsForm)
                    .build();
            contextMenu.getItems().addAll(rename);
            if(!tableViewState.isOpen()) {
                MenuItem openTable = new ContextMenuItemBuilder()
                        .withLabel(Resources.UI.getString("ContextMenu.openTable"))
                        .withClickHandlerControl(tableController::openTable)
                        .build();
                contextMenu.getItems().addAll(openTable);
            }
        }
        return contextMenu;
    }
}
