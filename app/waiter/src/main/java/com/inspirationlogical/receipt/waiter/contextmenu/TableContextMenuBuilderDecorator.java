package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.controller.restaurant.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import com.inspirationlogical.receipt.waiter.controller.table.TableViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class TableContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    private TableConfigurationController tableConfigurationController;
    private TableController tableController;

    public TableContextMenuBuilderDecorator(TableConfigurationController tableConfigurationController,
                                            TableController tableController,
                                            ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
        this.tableConfigurationController = tableConfigurationController;
        this.tableController = tableController;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        TableViewState tableViewState = (TableViewState) viewState;
        ContextMenu contextMenu = super.build(viewState);

        if (tableViewState.getRestaurantViewState().getMotionViewState().getMovableProperty().getValue()) {
            return contextMenu;
        }

        MenuItem editTable = buildMenuItem("ContextMenu.EditTable", tableConfigurationController::showEditTableForm);
        contextMenu.getItems().add(editTable);

        if (isConfigurationMode(tableViewState)) {
            MenuItem rotateTable = buildMenuItem("ContextMenu.RotateTable", tableConfigurationController::rotateTable);
            contextMenu.getItems().add(rotateTable);
            if (tableViewState.isSelected()) {
                MenuItem mergeTables = buildMenuItem("ContextMenu.MergeTable",tableConfigurationController::mergeTables);
                contextMenu.getItems().add(mergeTables);
            }
            if (tableViewState.isAggregate()) {
                MenuItem splitTables = buildMenuItem("ContextMenu.SplitTable", tableConfigurationController::splitTables);
                contextMenu.getItems().add(splitTables);
            } else if(tableViewState.isOpen() || tableViewState.isHost()) {
                // Delete is not allowed for open and host tables.
            } else {
                MenuItem deleteTable = buildMenuItem("ContextMenu.DeleteTable", tableConfigurationController::deleteTable);
                contextMenu.getItems().add(deleteTable);
            }
        } else {
            if(!tableViewState.isOpen()) {
                MenuItem openTable = buildMenuItem("ContextMenu.OpenTable", tableController::openTable);
                contextMenu.getItems().add(openTable);
            }
        }
        return contextMenu;
    }

    private Boolean isConfigurationMode(TableViewState tableViewState) {
        return tableViewState.getRestaurantViewState().getConfigurable().getValue();
    }
}
