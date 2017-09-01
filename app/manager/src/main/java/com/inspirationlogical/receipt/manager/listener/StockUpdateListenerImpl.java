package com.inspirationlogical.receipt.manager.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.manager.controller.stock.StockController;

@Singleton
public class StockUpdateListenerImpl implements StockListener.StockUpdateListener {

    @Inject
    private StockController stockController;

    @Override
    public void finished() {
        stockController.updateStockItems();
    }
}
