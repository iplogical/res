package com.inspirationlogical.receipt.waiter.decorator;

import com.inspirationlogical.receipt.waiter.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

public class TableContextMenuDecorator extends AbstractContextMenuDecorator {

    public TableContextMenuDecorator(ContextMenuBuilder contextMenuBuilder) {
        super(contextMenuBuilder);
    }

    @Override
    public ContextMenu build(ViewState viewState){
        // add table specific menu items
        return super.build(viewState);
    }
}
