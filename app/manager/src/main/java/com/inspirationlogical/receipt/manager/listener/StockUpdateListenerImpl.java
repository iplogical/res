package com.inspirationlogical.receipt.manager.listener;

import com.inspirationlogical.receipt.corelib.model.listeners.StockListener;
import com.inspirationlogical.receipt.manager.controller.stock.StockController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockUpdateListenerImpl implements StockListener.StockUpdateListener {

    @Autowired
    private StockController stockController;

    @Override
    public void finished() {
        stockController.updateStockItems();
    }
}
