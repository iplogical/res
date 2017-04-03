package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;

import com.inspirationlogical.receipt.corelib.model.view.StockView;

import lombok.Data;

@Data
public class StockViewModel extends ProductViewModel {
    private String soldQuantity;
    private String startingStock;
    private String date;

    public StockViewModel(StockView stockView) {
        super(stockView.getProduct());
        soldQuantity = valueOf(stockView.getSoldQuantity());
        startingStock = valueOf(stockView.getStartingStock());
        date = stockView.getDate().toString();
    }
}
