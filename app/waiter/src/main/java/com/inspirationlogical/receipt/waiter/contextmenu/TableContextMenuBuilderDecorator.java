package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;

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

    @Autowired
    private RestaurantController restaurantController;

    private TableController tableController;

    public TableContextMenuBuilderDecorator(TableController tableController,
                                            ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
        this.tableController = tableController;
    }

    @Override
    public ContextMenu build() {
        ContextMenu contextMenu = super.build();

        if (restaurantController.isMotionMode()) {
            return contextMenu;
        }

        MenuItem editTable = buildMenuItem("ContextMenu.EditTable", tableConfigurationController::showEditTableForm);
        contextMenu.getItems().add(editTable);

        if (restaurantController.isConfigurationMode()) {
            MenuItem rotateTable = buildMenuItem("ContextMenu.RotateTable", tableConfigurationController::rotateTable);
            contextMenu.getItems().add(rotateTable);
            if (tableController.isSelected()) {
                MenuItem exchangeTables = buildMenuItem("ContextMenu.ExchangeTable",tableConfigurationController::exchangeTables);
                contextMenu.getItems().add(exchangeTables);
            }
            if(!tableController.isOpen()) {
                MenuItem deleteTable = buildMenuItem("ContextMenu.DeleteTable", tableConfigurationController::deleteTable);
                contextMenu.getItems().add(deleteTable);
                MenuItem reOpenTable = buildMenuItem("ContextMenu.ReOpenTable", tableController::reOpenTable);
                contextMenu.getItems().add(reOpenTable);
            }
        } else {
            if(!tableController.isOpen()) {
                MenuItem openTable = buildMenuItem("ContextMenu.OpenTable", tableController::openTable);
                contextMenu.getItems().add(openTable);
            }
        }
        return contextMenu;
    }
}
