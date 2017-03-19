package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

public class TableContextMenuBuilderDecorator extends ContextMenuBuilderDecorator {

    public TableContextMenuBuilderDecorator(ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        ContextMenu contextMenu = super.build(viewState);
        // add table specific menu items
        return contextMenu;
    }
}
