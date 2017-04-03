package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.entity.Stock;

public class StockAdapter extends AbstractAdapter<Stock> {

    public StockAdapter(Stock adaptee) {
        super(adaptee);
    }

    public static List<StockAdapter> getItems(EntityManager manager) {
        return ((List<Stock>) manager.createNamedQuery(Stock.STOCK_GET_ITEMS).getResultList())
                .stream()
                .map(stock -> new StockAdapter(stock))
                .collect(Collectors.toList());
    }
}
