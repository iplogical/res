package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.Stock;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import java.util.List;
import java.util.Optional;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

public class StockAdapter extends AbstractAdapter<Stock> {

    public StockAdapter(Stock adaptee) {
        super(adaptee);
    }

    public static List<StockAdapter> getItems() {
        return ProductAdapter.getStorableProducts().stream()
                .map(StockAdapter::getLatestItemByProduct)
                .collect(toList());
    }

    public static void updateStock(ReceiptRecordAdapter receiptRecordAdapter, Optional<ReceiptType> receiptType) {
        receiptRecordAdapter.getAdaptee().getProduct().getRecipes().forEach(recipe ->
        {
            StockAdapter stockAdapter = StockAdapter.getLatestItemByProduct(new ProductAdapter(recipe.getComponent()));
            double quantity = receiptType.filter(type -> type.equals(ReceiptType.SALE))
                    .map(type -> recipe.getQuantityMultiplier() * receiptRecordAdapter.getAdaptee().getSoldQuantity())
                    .orElse(receiptRecordAdapter.getAdaptee().getAbsoluteQuantity());
            stockAdapter.updateStockAdapter(roundToTwoDecimals(quantity), receiptType.get());
        });
    }

    public static StockAdapter getLatestItemByProduct(ProductAdapter productAdapter) {
        List<Stock> stockList = GuardedTransaction.runNamedQuery(Stock.STOCK_GET_LATEST_ITEM_BY_PRODUCT,
                query -> query.setParameter("product", productAdapter.getAdaptee()).setMaxResults(1));
        if(stockList.isEmpty()) {
            return createStockEntry(productAdapter, 0);
        }
        return new StockAdapter(stockList.get(0));
    }


    public static void closeLatestStockEntries() {
        ProductAdapter.getStorableProducts().forEach(productAdapter ->
            {
                StockAdapter stock = getLatestItemByProduct(productAdapter);
                if(stock.isStockChanged())
                    createStockEntry(productAdapter, getInitialQuantity(stock));
            }
        );
    }

    private boolean isStockChanged() {
        return !((adaptee.getSoldQuantity() == 0) && (adaptee.getPurchasedQuantity() == 0) && (adaptee.getInventoryQuantity() == 0) && (adaptee.getDisposedQuantity() == 0));
    }

    private static StockAdapter createStockEntry(ProductAdapter productAdapter, double initialQuantity) {
        Stock stock = Stock.builder()
                .owner(productAdapter.getAdaptee())
                .initialQuantity(initialQuantity)
                .soldQuantity(0)
                .purchasedQuantity(0)
                .inventoryQuantity(0)
                .disposedQuantity(0)
                .date(now())
                .build();
        GuardedTransaction.persist(stock);
        return new StockAdapter(stock);
    }

    private static double getInitialQuantity(StockAdapter stock) {
        return stock.getAdaptee().getInitialQuantity()
                - stock.getAdaptee().getSoldQuantity()
                + stock.getAdaptee().getPurchasedQuantity()
                + stock.getAdaptee().getInventoryQuantity() // Inventory can be negative.
                - stock.getAdaptee().getDisposedQuantity();
    }

    public void updateStockAdapter(double quantity, ReceiptType type) {
        if(ReceiptType.isSale(type)) {
            increaseSoldQuantity(quantity);
        } else if(ReceiptType.isPurchase(type)){
            increasePurchasedQuantity(quantity);
        } else if(ReceiptType.isInventory(type)) {
            increaseInventoryQuantity(quantity);
        } else if(ReceiptType.isDisposal(type)) {
            increaseDisposedQuantity(quantity);
        } else {
            // OTHER type Receipt, do nothing.
        }
    }

    private void increaseSoldQuantity(double quantity) {
        GuardedTransaction.run(() -> adaptee.setSoldQuantity(adaptee.getSoldQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
    }

    private void increasePurchasedQuantity(double quantity) {
        GuardedTransaction.run(() -> adaptee.setPurchasedQuantity(adaptee.getPurchasedQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
    }

    private void increaseInventoryQuantity(double quantity) {
        GuardedTransaction.run(() -> adaptee.setInventoryQuantity(adaptee.getInventoryQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
    }

    private void increaseDisposedQuantity(double quantity) {
        GuardedTransaction.run(() -> adaptee.setDisposedQuantity(adaptee.getDisposedQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
    }
}
