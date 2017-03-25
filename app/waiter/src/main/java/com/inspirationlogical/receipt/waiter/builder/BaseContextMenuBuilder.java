package com.inspirationlogical.receipt.waiter.builder;

import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.scene.control.ContextMenu;

/**
 * Created by Ferenc on 2017. 03. 19..
 */
public class BaseContextMenuBuilder implements ContextMenuBuilder {
    @Override
    public ContextMenu build(ViewState viewState) {
        ContextMenu contextMenu = new ContextMenu();
        return contextMenu;
    }
}
