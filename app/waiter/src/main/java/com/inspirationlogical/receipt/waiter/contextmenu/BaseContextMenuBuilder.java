package com.inspirationlogical.receipt.waiter.contextmenu;

import static javafx.stage.PopupWindow.AnchorLocation.CONTENT_BOTTOM_RIGHT;

import com.inspirationlogical.receipt.corelib.frontend.contextmenu.ContextMenuBuilder;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

/**
 * Created by Ferenc on 2017. 03. 19..
 */
public class BaseContextMenuBuilder implements ContextMenuBuilder {
    @Override
    public ContextMenu build(ViewState viewState) {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setAnchorLocation(CONTENT_BOTTOM_RIGHT);
        return contextMenu;
    }
}
