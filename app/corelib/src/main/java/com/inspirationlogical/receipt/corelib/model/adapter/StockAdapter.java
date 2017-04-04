package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.entity.Stock;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

public class StockAdapter extends AbstractAdapter<Stock> {

    public StockAdapter(Stock adaptee) {
        super(adaptee);
    }

    public static List<StockAdapter> getItems(EntityManager manager) {
        return ((List<Stock>) manager.createNamedQuery(Stock.STOCK_GET_ITEMS).getResultList())
                .stream()
                .map(stock -> new StockAdapter(stock))
                .collect(toList());
    }

    // TODO: Implement daily closure to create the stock entries of the next day.
    public static StockAdapter getLatestItemByProduct(ProductAdapter productAdapter) {
        List<Stock> stockList = GuardedTransaction.RunNamedQuery(Stock.STOCK_GET_ITEM_BY_PRODUCT,
                query -> query.setParameter("product", productAdapter.getAdaptee()));
        return stockList.stream()
                .map(stock -> new StockAdapter(stock))
                .max(Comparator.comparing(stockAdapter -> stockAdapter.getAdaptee().getDate()))
                .orElseGet(() -> createStockEntry(productAdapter));
    }

    private static StockAdapter createStockEntry(ProductAdapter productAdapter) {
        GuardedTransaction.RunWithRefresh(productAdapter.getAdaptee(), () -> {});
            Stock stock = Stock.builder()
                    .owner(productAdapter.getAdaptee())
                    .initialQuantity(50)
                    .soldQuantity(0)
                    .purchasedQuantity(0)
                    .date(now())
                    .build();
        GuardedTransaction.Persist(stock);
        return new StockAdapter(stock);
    }

    public void updateStockAdapter(double quantity, ReceiptType type) {
        if(ReceiptType.isStockDecrement(type)) {
            decreaseStock(quantity);
        } else if(ReceiptType.isStockIncrement(type)){
            increaseStock(quantity);
        } else if(ReceiptType.isInventory(type)) {
            if(quantity > 0) {
                decreaseStock(quantity);
            } else {
                increaseStock(Math.abs(quantity));
            }
        } else {
            // OTHER type Receipt, do nothing.
        }
    }

    private void decreaseStock(double quantity) {
        adaptee.setSoldQuantity(adaptee.getSoldQuantity() + quantity);
    }

    private void increaseStock(double quantity) {
        adaptee.setPurchasedQuantity(adaptee.getPurchasedQuantity() + quantity);
    }

}
