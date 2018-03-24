package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.Stock;

public class StockAdapter extends AbstractAdapter<Stock> {

    public StockAdapter(Stock adaptee) {
        super(adaptee);
    }

//    public static List<StockAdapter> getItems() {
//        return ProductAdapter.getStorableProducts().stream()
//                .map(StockAdapter::getLatestItemByProduct)
//                .collect(toList());
//    }
//
//    public static void updateStock(ReceiptRecord receiptRecord, ReceiptType receiptType) {
//        updateStockRecords(receiptRecord, receiptType, true);
//    }
//
//    public static void decreaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType) {
//        updateStockRecords(receiptRecord, receiptType, false);
//    }
//
//    private static void updateStockRecords(ReceiptRecord receiptRecord, ReceiptType receiptType, boolean increase) {
//        List<Recipe> recipes = GuardedTransaction.runNamedQuery(GET_RECIPES_OF_PRODUCT, query -> query.setParameter("owner", receiptRecord.getProduct()));
//        recipes.forEach(recipe ->
//        {
//            StockAdapter stockAdapter = StockAdapter.getLatestItemByProduct(new ProductAdapter(recipe.getComponent()));
//            double quantity = calculateStockQuantity(receiptRecord, receiptType, recipe);
//            if(increase) {
//                stockAdapter.updateStockAdapter(roundToTwoDecimals(quantity), receiptType);
//            } else {
//                stockAdapter.decreaseStockAdapter(roundToTwoDecimals(quantity), receiptType);
//            }
//            GuardedTransaction.detach(stockAdapter.getAdaptee());
//            GuardedTransaction.detach(recipe);
//        });
//    }
//
//    private static Double calculateStockQuantity(ReceiptRecord receiptRecord, ReceiptType receiptType, Recipe recipe) {
//        return Optional.of(receiptType).filter(type -> type.equals(ReceiptType.SALE))
//                .map(type -> recipe.getQuantityMultiplier() * receiptRecord.getSoldQuantity())
//                .orElse(receiptRecord.getAbsoluteQuantity());
//    }
//
//    static StockAdapter getLatestItemByProduct(ProductAdapter productAdapter) {
//        List<Stock> stockList = GuardedTransaction.runNamedQuery(Stock.STOCK_GET_LATEST_ITEM_BY_PRODUCT,
//                query -> query.setParameter("product", productAdapter.getAdaptee()).setMaxResults(1));
//        if(stockList.isEmpty()) {
//            return createStockEntry(productAdapter, 0);
//        }
//        return new StockAdapter(stockList.get(0));
//    }
//
//
//    public static void closeLatestStockEntries() {
//        ProductAdapter.getStorableProducts().forEach(productAdapter ->
//            {
//                StockAdapter stock = getLatestItemByProduct(productAdapter);
//                StockAdapter newStock;
//                if(stock.isStockChanged()) {
//                    newStock = createStockEntry(productAdapter, getInitialQuantity(stock));
//                    GuardedTransaction.detach(newStock.getAdaptee());
//                }
//                GuardedTransaction.detach(stock.getAdaptee());
//            }
//        );
//        List<Recipe> recipes = GuardedTransaction.runNamedQuery(Recipe.GET_TEST_RECIPES);
//        recipes.forEach(GuardedTransaction::detach);
//    }
//
//    private boolean isStockChanged() {
//        return !((adaptee.getSoldQuantity() == 0) && (adaptee.getPurchasedQuantity() == 0) && (adaptee.getInventoryQuantity() == 0) && (adaptee.getDisposedQuantity() == 0));
//    }
//
//    private static StockAdapter createStockEntry(ProductAdapter productAdapter, double initialQuantity) {
//        Stock stock = Stock.builder()
//                .owner(productAdapter.getAdaptee())
//                .initialQuantity(initialQuantity)
//                .soldQuantity(0)
//                .purchasedQuantity(0)
//                .inventoryQuantity(0)
//                .disposedQuantity(0)
//                .date(now())
//                .build();
//        GuardedTransaction.persist(stock);
//        return new StockAdapter(stock);
//    }
//
//    private static double getInitialQuantity(StockAdapter stock) {
//        return stock.getAdaptee().getInitialQuantity()
//                - stock.getAdaptee().getSoldQuantity()
//                + stock.getAdaptee().getPurchasedQuantity()
//                + stock.getAdaptee().getInventoryQuantity() // Inventory can be negative.
//                - stock.getAdaptee().getDisposedQuantity();
//    }
//
//    public void updateStockAdapter(double quantity, ReceiptType type) {
//        if(ReceiptType.isSale(type)) {
//            increaseSoldQuantity(quantity);
//        } else if(ReceiptType.isPurchase(type)){
//            increasePurchasedQuantity(quantity);
//        } else if(ReceiptType.isInventory(type)) {
//            increaseInventoryQuantity(quantity);
//        } else if(ReceiptType.isDisposal(type)) {
//            increaseDisposedQuantity(quantity);
//        } else {
//            // OTHER type Receipt, do nothing.
//        }
//    }
//
//    private void increaseSoldQuantity(double quantity) {
//        GuardedTransaction.run(() -> adaptee.setSoldQuantity(adaptee.getSoldQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
//    }
//
//    private void increasePurchasedQuantity(double quantity) {
//        GuardedTransaction.run(() -> adaptee.setPurchasedQuantity(adaptee.getPurchasedQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
//    }
//
//    private void increaseInventoryQuantity(double quantity) {
//        GuardedTransaction.run(() -> adaptee.setInventoryQuantity(adaptee.getInventoryQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
//    }
//
//    private void increaseDisposedQuantity(double quantity) {
//        GuardedTransaction.run(() -> adaptee.setDisposedQuantity(adaptee.getDisposedQuantity() + quantity / adaptee.getOwner().getStorageMultiplier()));
//    }
//
//    private void decreaseStockAdapter(double quantity, ReceiptType type) {
//        if(ReceiptType.isSale(type)) {
//            decreaseSoldQuantity(quantity);
//        } else {
//            // ONLY SALE RECEIPTS CAN BE REOPENED.
//        }
//    }
//
//    private void decreaseSoldQuantity(double quantity) {
//        GuardedTransaction.run(() -> adaptee.setSoldQuantity(adaptee.getSoldQuantity() - quantity / adaptee.getOwner().getStorageMultiplier()));
//    }
}
