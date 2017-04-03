package com.inspirationlogical.receipt.corelib.model.view;

import java.time.LocalDateTime;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;

public class StockViewImpl extends AbstractModelViewImpl<StockAdapter>
        implements StockView {

    public StockViewImpl(StockAdapter adapter) {
        super(adapter);
    }

    @Override
    public ProductView getProduct() {
        return new ProductViewImpl(new ProductAdapter(adapter.getAdaptee().getOwner()));
    }

    @Override
    public double getSoldQuantity() {
        return adapter.getAdaptee().getSoldQuantity();
    }

    @Override
    public double getStartingStock() {
        return adapter.getAdaptee().getStartingStock();
    }

    @Override
    public LocalDateTime getDate() {
        return adapter.getAdaptee().getDate();
    }
}
