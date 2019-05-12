package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Stock;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;

@Getter
@ToString
public class StockView {

    private ProductView product;
    private double initialQuantity;
    private double soldQuantity;
    private double purchasedQuantity;
    private double inventoryQuantity;
    private double disposedQuantity;
    private double availableQuantity;
    @Setter
    private Double inputQuantity;
    private LocalDateTime date;

    public StockView(Stock stock) {
        product = new ProductView(stock.getOwner());
        initialQuantity = roundToTwoDecimals(stock.getInitialQuantity());
        soldQuantity = roundToTwoDecimals(stock.getSoldQuantity());
        purchasedQuantity = roundToTwoDecimals(stock.getPurchasedQuantity());
        inventoryQuantity = roundToTwoDecimals(stock.getInventoryQuantity());
        disposedQuantity = roundToTwoDecimals(stock.getDisposedQuantity());
        availableQuantity = roundToTwoDecimals(calculateAvailableQuantity());
        date = stock.getDate();
    }

    private double calculateAvailableQuantity() {
        return initialQuantity
                - soldQuantity
                + purchasedQuantity
                + inventoryQuantity
                - disposedQuantity;
    }

    public String getAvailableQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(availableQuantity * product.getStorageMultiplier()));
    }

    public String getInitialQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(initialQuantity * product.getStorageMultiplier()));
    }

    public String getSoldQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(soldQuantity * product.getStorageMultiplier()));
    }

    public String getPurchasedQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(purchasedQuantity * product.getStorageMultiplier()));
    }

    public String getInventoryQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(inventoryQuantity * product.getStorageMultiplier()));
    }

    public String getDisposedQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(disposedQuantity * product.getStorageMultiplier()));
    }
}
