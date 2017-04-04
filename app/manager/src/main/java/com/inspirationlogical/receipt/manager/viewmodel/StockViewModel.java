package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;

import com.inspirationlogical.receipt.corelib.model.view.StockView;

import lombok.Data;

@Data
public class StockViewModel extends ProductViewModel {
    private String availableQuantity;
    private String initialQuantity;
    private String soldQuantity;
    private String purchasedQuantity;
    private String date;

    public StockViewModel(StockView stockView) {
        super(stockView.getProduct());
        double available = stockView.getInitialQuantity() - stockView.getSoldQuantity() + stockView.getPurchasedQuantity();
        availableQuantity = valueOf(available);
        initialQuantity = valueOf(stockView.getInitialQuantity());
        soldQuantity = valueOf(stockView.getSoldQuantity());
        purchasedQuantity = valueOf(stockView.getPurchasedQuantity());
        date = stockView.getDate().toString();
    }
}
