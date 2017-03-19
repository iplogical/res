package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class TableContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    public TableContextMenuBuilderDecorator(ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        ContextMenu contextMenu = super.build(viewState);
        contextMenu.getItems().add(new MenuItem("Table Lufasz"));
        // add table specific menu items
        return contextMenu;
    }
}
