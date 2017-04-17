package com.inspirationlogical.receipt.corelib.frontend.builder;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

public interface ContextMenuBuilder {
    ContextMenu build(ViewState viewState);
}
