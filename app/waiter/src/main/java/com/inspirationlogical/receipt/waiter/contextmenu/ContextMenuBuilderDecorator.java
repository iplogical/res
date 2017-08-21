package com.inspirationlogical.receipt.waiter.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;

import java.util.function.Consumer;

public abstract class ContextMenuBuilderDecorator implements ContextMenuBuilder {
    private ContextMenuBuilder contextMenuBuilder;

    ContextMenuBuilderDecorator(ContextMenuBuilder contextMenuBuilder) {
        this.contextMenuBuilder = contextMenuBuilder;
    }

    @Override
    public ContextMenu build(ViewState viewState) {
        return contextMenuBuilder.build(viewState);
    }

    MenuItem buildMenuItem(String name, Consumer<Control> handler) {
        return new ContextMenuItemBuilder()
                .withLabel(WaiterResources.WAITER.getString(name))
                .withClickHandlerControl(handler)
                .build();
    }

    MenuItem buildMenuItem(String name, Runnable handler) {
        return new ContextMenuItemBuilder()
                .withLabel(WaiterResources.WAITER.getString(name))
                .withClickHandler(handler)
                .build();
    }
}
