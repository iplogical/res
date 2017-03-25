package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

public interface ContextMenuBuilder {
    ContextMenu build(ViewState viewState);
}
