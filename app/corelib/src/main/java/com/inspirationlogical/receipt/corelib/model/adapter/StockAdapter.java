package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.Stock;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

public class StockAdapter extends AbstractAdapter<Stock> {

    public StockAdapter(Stock adaptee) {
        super(adaptee);
    }

    public static List<StockAdapter> getItems() {
        return ProductCategoryAdapter.getRootCategory().getAllStorableProducts().stream()
                .map(productAdapter -> getLatestItemByProduct(productAdapter))
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
        List<Stock> stockList = GuardedTransaction.RunNamedQuery(Stock.STOCK_GET_ITEM_BY_PRODUCT,
                query -> query.setParameter("product", productAdapter.getAdaptee()));
        return stockList.stream()
                .map(stock -> new StockAdapter(stock))
                .max(Comparator.comparing(stockAdapter -> stockAdapter.getAdaptee().getDate()))
                .orElseGet(() -> createStockEntry(productAdapter, 0));
    }

    public static void closeLatestStockEntries() {
        Set<ProductAdapter> storableProducts =
        ProductCategoryAdapter.getRootCategory().getAllStorableProducts();
        storableProducts.forEach(productAdapter ->
            {
                StockAdapter stock = getLatestItemByProduct(productAdapter);
                createStockEntry(productAdapter, getInitialQuantity(stock));
            }
        );
    }

    private static StockAdapter createStockEntry(ProductAdapter productAdapter, double initialQuantity) {
        GuardedTransaction.RunWithRefresh(productAdapter.getAdaptee(), () -> {});
            Stock stock = Stock.builder()
                    .owner(productAdapter.getAdaptee())
                    .initialQuantity(initialQuantity)
                    .soldQuantity(0)
                    .purchasedQuantity(0)
                    .date(now())
                    .build();
        GuardedTransaction.Persist(stock);
        return new StockAdapter(stock);
    }

    private static double getInitialQuantity(StockAdapter stock) {
        return stock.getAdaptee().getInitialQuantity() - stock.getAdaptee().getSoldQuantity() + stock.getAdaptee().getPurchasedQuantity();
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
        GuardedTransaction.Run(() -> adaptee.setSoldQuantity(adaptee.getSoldQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
    }

    private void increaseStock(double quantity) {
        GuardedTransaction.Run(() -> adaptee.setPurchasedQuantity(adaptee.getPurchasedQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
    }
}
