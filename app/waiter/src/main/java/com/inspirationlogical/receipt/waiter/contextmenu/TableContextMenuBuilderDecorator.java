package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import com.inspirationlogical.receipt.waiter.controller.table.TableViewState;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class TableContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    @Autowired
    private TableConfigurationController tableConfigurationController;

    private TableController tableController;

    public TableContextMenuBuilderDecorator(TableController tableController,
                                            ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
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
                MenuItem exchangeTables = buildMenuItem("ContextMenu.ExchangeTable",tableConfigurationController::exchangeTables);
                contextMenu.getItems().add(exchangeTables);
            }
            if(!tableController.getView().isOpen()) {
                MenuItem deleteTable = buildMenuItem("ContextMenu.DeleteTable", tableConfigurationController::deleteTable);
                contextMenu.getItems().add(deleteTable);
                MenuItem reOpenTable = buildMenuItem("ContextMenu.ReOpenTable", tableController::reOpenTable);
                contextMenu.getItems().add(reOpenTable);
            }
        } else {
            if(!tableController.getView().isOpen()) {
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
