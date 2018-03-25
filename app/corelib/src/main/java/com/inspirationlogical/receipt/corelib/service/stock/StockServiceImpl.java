package com.inspirationlogical.receipt.corelib.service.stock;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.entity.Recipe;
import com.inspirationlogical.receipt.corelib.model.entity.Stock;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.model.view.StockView;
import com.inspirationlogical.receipt.corelib.model.view.StockViewImpl;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.repository.RecipeRepository;
import com.inspirationlogical.receipt.corelib.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<StockView> getItems() {
        return productRepository.getStorableProducts().stream()
                .map(this::getLatestItemByProduct)
                .map(StockViewImpl::new)
                .collect(toList());
    }

    @Override
    public void increaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType) {
        updateStockRecords(receiptRecord, receiptType, true);
    }

    @Override
    public void decreaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType) {
        updateStockRecords(receiptRecord, receiptType, false);
    }

    private void updateStockRecords(ReceiptRecord receiptRecord, ReceiptType receiptType, boolean increase) {
        List<Recipe> recipes = recipeRepository.findAllByOwner(receiptRecord.getProduct());
        recipes.forEach(recipe ->
        {
            Stock stock = getLatestItemByProduct(recipe.getComponent());
            double quantity = calculateStockQuantity(receiptRecord, receiptType, recipe);
            if(increase) {
                increaseStock(stock, roundToTwoDecimals(quantity), receiptType);
            } else {
                decreaseStock(stock, roundToTwoDecimals(quantity), receiptType);
            }
            stockRepository.save(stock);
        });
    }

    private Stock getLatestItemByProduct(Product product) {
        Stock latestStock = stockRepository.findFirstByOwnerOrderByDateDesc(product);
        if(latestStock == null) {
            return createStockEntry(product, 0);
        }
        return latestStock;
    }

    private Stock createStockEntry(Product product, double initialQuantity) {
        Stock stock = Stock.builder()
                .owner(product)
                .initialQuantity(initialQuantity)
                .soldQuantity(0)
                .purchasedQuantity(0)
                .inventoryQuantity(0)
                .disposedQuantity(0)
                .date(now())
                .build();
        stockRepository.save(stock);
        return stock;
    }

    private Double calculateStockQuantity(ReceiptRecord receiptRecord, ReceiptType receiptType, Recipe recipe) {
        return Optional.of(receiptType).filter(type -> type.equals(ReceiptType.SALE))
                .map(type -> recipe.getQuantityMultiplier() * receiptRecord.getSoldQuantity())
                .orElse(receiptRecord.getAbsoluteQuantity());
    }

    @Override
    public void closeLatestStockEntries() {
        productRepository.getStorableProducts().forEach(productAdapter ->
                {
                    Stock stock = getLatestItemByProduct(productAdapter);
                    if(isStockChanged(stock)) {
                        createStockEntry(productAdapter, getInitialQuantity(stock));
                    }
                }
        );
    }

    private boolean isStockChanged(Stock stock) {
        return !((stock.getSoldQuantity() == 0) && (stock.getPurchasedQuantity() == 0) && (stock.getInventoryQuantity() == 0) && (stock.getDisposedQuantity() == 0));
    }

    private double getInitialQuantity(Stock stock) {
        return stock.getInitialQuantity()
                - stock.getSoldQuantity()
                + stock.getPurchasedQuantity()
                + stock.getInventoryQuantity() // Inventory can be negative.
                - stock.getDisposedQuantity();
    }

    private void increaseStock(Stock stock, double quantity, ReceiptType type) {
        if(ReceiptType.isSale(type)) {
            increaseSoldQuantity(stock, quantity);
        } else if(ReceiptType.isPurchase(type)){
            increasePurchasedQuantity(stock, quantity);
        } else if(ReceiptType.isInventory(type)) {
            increaseInventoryQuantity(stock, quantity);
        } else if(ReceiptType.isDisposal(type)) {
            increaseDisposedQuantity(stock, quantity);
        } else {
            // OTHER type Receipt, do nothing.
        }
    }

    private void increaseSoldQuantity(Stock stock, double quantity) {
        stock.setSoldQuantity(stock.getSoldQuantity() + quantity / stock.getOwner().getStorageMultiplier());
    }

    private void increasePurchasedQuantity(Stock stock, double quantity) {
        stock.setPurchasedQuantity(stock.getPurchasedQuantity() + quantity / stock.getOwner().getStorageMultiplier());
    }

    private void increaseInventoryQuantity(Stock stock, double quantity) {
        stock.setInventoryQuantity(stock.getInventoryQuantity() + quantity / stock.getOwner().getStorageMultiplier());
    }

    private void increaseDisposedQuantity(Stock stock, double quantity) {
        stock.setDisposedQuantity(stock.getDisposedQuantity() + quantity / stock.getOwner().getStorageMultiplier());
    }

    private void decreaseStock(Stock stock, double quantity, ReceiptType type) {
        if(ReceiptType.isSale(type)) {
            decreaseSoldQuantity(stock, quantity);
        } else {
            // ONLY SALE RECEIPTS CAN BE REOPENED.
        }
    }

    private void decreaseSoldQuantity(Stock stock, double quantity) {
        GuardedTransaction.run(() -> stock.setSoldQuantity(stock.getSoldQuantity() - quantity / stock.getOwner().getStorageMultiplier()));
    }

}
