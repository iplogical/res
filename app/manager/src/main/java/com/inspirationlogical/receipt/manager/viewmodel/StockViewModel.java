package com.inspirationlogical.receipt.manager.viewmodel;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.lang.String.valueOf;

import com.inspirationlogical.receipt.corelib.model.view.StockView;

import lombok.Data;

@Data
public class StockViewModel extends ProductViewModel {
    private String availableQuantity;
    private String initialQuantity;
    private String soldQuantity;
    private String inputQuantity;
    private String purchasedQuantity;
    private String date;

    public StockViewModel(StockView stockView) {
        super(stockView.getProduct());
        double available = roundToTwoDecimals(stockView.getInitialQuantity() - stockView.getSoldQuantity() + stockView.getPurchasedQuantity());
        availableQuantity = valueOf(available);
        initialQuantity = valueOf(roundToTwoDecimals(stockView.getInitialQuantity()));
        soldQuantity = valueOf(roundToTwoDecimals(stockView.getSoldQuantity()));
        purchasedQuantity = valueOf(roundToTwoDecimals(stockView.getPurchasedQuantity()));
        date = stockView.getDate().toString();
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
}
