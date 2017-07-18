package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import com.inspirationlogical.receipt.waiter.controller.table.TableViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

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

        if (tableViewState.getRestaurantViewState().getMotionViewState().getMovableProperty().getValue()) {
            return contextMenu;
        }

        MenuItem editTable = new ContextMenuItemBuilder()
                .withLabel(Resources.WAITER.getString("ContextMenu.EditTable"))
                .withClickHandlerControl(restaurantController::showEditTableForm)
                .build();
        contextMenu.getItems().add(editTable);

        if (tableViewState.getRestaurantViewState().getConfigurable().getValue()) {
            MenuItem rotateTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.WAITER.getString("ContextMenu.RotateTable"))
                    .withClickHandlerControl(restaurantController::rotateTable)
                    .build();
            MenuItem deleteTable = new ContextMenuItemBuilder()
                    .withLabel(Resources.WAITER.getString("ContextMenu.DeleteTable"))
                    .withClickHandlerControl(restaurantController::deleteTable)
                    .build();
            contextMenu.getItems().addAll(rotateTable, deleteTable);
            if (tableViewState.isSelected()) {
                MenuItem mergeTables = new ContextMenuItemBuilder()
                        .withLabel(Resources.WAITER.getString("ContextMenu.MergeTable"))
                        .withClickHandler(restaurantController::mergeTables)
                        .build();
                contextMenu.getItems().add(mergeTables);
            }
            if (tableViewState.isAggregate()) {
                MenuItem splitTables = new ContextMenuItemBuilder()
                        .withLabel(Resources.WAITER.getString("ContextMenu.SplitTable"))
                        .withClickHandlerControl(restaurantController::splitTables)
                        .build();
                contextMenu.getItems().add(splitTables);
            }
        } else {
            if(!tableViewState.isOpen()) {
                MenuItem openTable = new ContextMenuItemBuilder()
                        .withLabel(Resources.WAITER.getString("ContextMenu.OpenTable"))
                        .withClickHandlerControl(tableController::openTable)
                        .build();
                contextMenu.getItems().add(openTable);
            }
        }
        return contextMenu;
    }
}
