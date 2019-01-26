package com.inspirationlogical.receipt.waiter.contextmenu;

import static javafx.stage.PopupWindow.AnchorLocation.CONTENT_BOTTOM_RIGHT;

import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;

import javafx.scene.control.ContextMenu;

public class BaseContextMenuBuilder implements ContextMenuBuilder {
    @Override
    public ContextMenu build() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAnchorLocation(CONTENT_BOTTOM_RIGHT);
        return contextMenu;
    }
}
