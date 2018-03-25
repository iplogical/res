package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Stock;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class StockViewImpl implements StockView {

    private ProductView product;
    private double initialQuantity;
    private double soldQuantity;
    private double purchasedQuantity;
    private double inventoryQuantity;
    private double disposedQuantity;
    private LocalDateTime date;

    public StockViewImpl(Stock stock) {
        product = new ProductViewImpl(stock.getOwner());
        initialQuantity = stock.getInitialQuantity();
        soldQuantity = stock.getSoldQuantity();
        purchasedQuantity = stock.getPurchasedQuantity();
        inventoryQuantity = stock.getInventoryQuantity();
        disposedQuantity = stock.getDisposedQuantity();
        date = stock.getDate();
    }
}
