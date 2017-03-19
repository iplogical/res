package com.inspirationlogical.receipt.waiter.decorator;

import com.inspirationlogical.receipt.waiter.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

public abstract class AbstractContextMenuDecorator implements ContextMenuBuilder {
    protected ContextMenuBuilder contextMenuBuilder;

    public AbstractContextMenuDecorator(ContextMenuBuilder contextMenuBuilder) {
        this.contextMenuBuilder = contextMenuBuilder;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        return contextMenuBuilder.build(viewState);
    }
}
