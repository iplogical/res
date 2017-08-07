package com.inspirationlogical.receipt.corelib.frontend.contextmenu;

import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

public interface ContextMenuBuilder {
    ContextMenu build(ViewState viewState);
}
