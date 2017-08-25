package com.inspirationlogical.receipt.manager.viewmodel;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.lang.String.valueOf;

import com.inspirationlogical.receipt.corelib.model.view.StockView;

import lombok.Data;

@Data
public class StockViewModel extends GoodsTableViewModel {
    private String availableQuantity;
    private String initialQuantity;
    private String soldQuantity;
    private String purchasedQuantity;
    private String inventoryQuantity;
    private String disposedQuantity;
    private String inputQuantity;
    private String date;

    public StockViewModel(StockView stockView) {
        super(stockView.getProduct());
        double available = stockView.getInitialQuantity()
                - stockView.getSoldQuantity()
                + stockView.getPurchasedQuantity()
                + stockView.getInventoryQuantity()
                - stockView.getDisposedQuantity();
        availableQuantity = valueOf(available);
        initialQuantity = valueOf(stockView.getInitialQuantity());
        soldQuantity = valueOf(stockView.getSoldQuantity());
        purchasedQuantity = valueOf(stockView.getPurchasedQuantity());
        inventoryQuantity = valueOf(stockView.getInventoryQuantity());
        disposedQuantity = valueOf(stockView.getDisposedQuantity());
        date = stockView.getDate().toString();
    }

    public String getAvailableQuantity() {
        return valueOf(roundToTwoDecimals(Double.valueOf(availableQuantity)));
    }

    public String getInitialQuantity() {
        return valueOf(roundToTwoDecimals(Double.valueOf(initialQuantity)));
    }

    public String getSoldQuantity() {
        return valueOf(roundToTwoDecimals(Double.valueOf(soldQuantity)));
    }

    public String getPurchasedQuantity() {
        return valueOf(roundToTwoDecimals(Double.valueOf(purchasedQuantity)));
    }

    public String getInventoryQuantity() {
        return valueOf(roundToTwoDecimals(Double.valueOf(inventoryQuantity)));
    }

    public String getDisposedQuantity() {
        return valueOf(roundToTwoDecimals(Double.valueOf(disposedQuantity)));
    }

    public String getAvailableQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(Double.valueOf(availableQuantity) * Double.valueOf(storageMultiplier)));
    }

    public String getInitialQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(Double.valueOf(initialQuantity) * Double.valueOf(storageMultiplier)));
    }

    public String getSoldQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(Double.valueOf(soldQuantity) * Double.valueOf(storageMultiplier)));
    }

    public String getPurchasedQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(Double.valueOf(purchasedQuantity) * Double.valueOf(storageMultiplier)));
    }

    public String getInventoryQuantityAbsolute() {
        return String.valueOf(roundToTwoDecimals(Double.valueOf(inventoryQuantity) * Double.valueOf(storageMultiplier)));
    }

}
