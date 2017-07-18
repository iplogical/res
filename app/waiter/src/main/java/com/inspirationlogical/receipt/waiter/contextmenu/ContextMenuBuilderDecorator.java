package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.builder.ContextMenuBuilder;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

public abstract class ContextMenuBuilderDecorator implements ContextMenuBuilder {
    protected ContextMenuBuilder contextMenuBuilder;

    public ContextMenuBuilderDecorator(ContextMenuBuilder contextMenuBuilder) {
        this.contextMenuBuilder = contextMenuBuilder;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        return contextMenuBuilder.build(viewState);
    }
}
