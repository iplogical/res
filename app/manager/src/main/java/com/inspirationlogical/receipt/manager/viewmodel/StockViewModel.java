package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;

import com.inspirationlogical.receipt.corelib.model.view.StockView;

import lombok.Data;

@Data
public class StockViewModel extends ProductViewModel {
    private String availableQuantity;
    private String soldQuantity;
    private String initialQuantity;
    private String date;

    public StockViewModel(StockView stockView) {
        super(stockView.getProduct());
        availableQuantity = valueOf(stockView.getInitialQuantity() - stockView.getSoldQuantity());
        soldQuantity = valueOf(stockView.getSoldQuantity());
        initialQuantity = valueOf(stockView.getInitialQuantity());
        date = stockView.getDate().toString();
    }
}
