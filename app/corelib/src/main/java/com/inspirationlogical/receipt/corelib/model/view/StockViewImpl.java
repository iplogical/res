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
    public double getInitialQuantity() {
        return adapter.getAdaptee().getInitialQuantity();
    }

    @Override
    public double getSoldQuantity() {
        return adapter.getAdaptee().getSoldQuantity();
    }

    @Override
    public double getPurchasedQuantity() {
        return adapter.getAdaptee().getPurchasedQuantity();
    }

    @Override
    public double getInventoryQuantity() {
        return adapter.getAdaptee().getInventoryQuantity();
    }

    @Override
    public double getDisposedQuantity() {
        return adapter.getAdaptee().getDisposedQuantity();
    }

    @Override
    public LocalDateTime getDate() {
        return adapter.getAdaptee().getDate();
    }
}
