package com.inspirationlogical.receipt.corelib.service.stock;

import com.inspirationlogical.receipt.corelib.model.entity.*;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.StockView;
import com.inspirationlogical.receipt.corelib.repository.ProductRepository;
import com.inspirationlogical.receipt.corelib.repository.RecipeRepository;
import com.inspirationlogical.receipt.corelib.repository.StockRepository;
import com.inspirationlogical.receipt.corelib.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.inspirationlogical.receipt.corelib.utility.Round.roundToTwoDecimals;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class StockServiceImpl implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Override
    public List<StockView> getStockViewListByCategory(ProductCategoryView selectedCategory) {
        List<ProductView> productViewList = productService.getProductsByCategory(selectedCategory, false);
        return productViewList.stream()
                .map(this::getLatestItemByProduct)
                .map(StockView::new)
                .collect(toList());
    }

    private Stock getLatestItemByProduct(ProductView productView) {
        Product product = productRepository.findById(productView.getId());
        return getLatestItemByProduct(product);
    }

    private Stock getLatestItemByProduct(Product product) {
        Stock latestStock = stockRepository.findFirstByOwnerOrderByDateDesc(product);
        if (latestStock == null) {
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

    @Override
    public void increaseStock(Receipt receipt, ReceiptType receiptType) {
        receipt.getRecords().forEach(receiptRecord -> increaseStock(receiptRecord, receipt.getType()));
    }

    private void increaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType) {
        List<Recipe> recipes = recipeRepository.findAllByOwner(receiptRecord.getProduct());
        recipes.forEach(recipe -> {
            Stock stock = getLatestItemByProduct(recipe.getComponent());
            double quantity = calculateStockQuantity(receiptRecord, receiptType, recipe);
            increaseStock(stock, roundToTwoDecimals(quantity), receiptType);
            stockRepository.save(stock);
        });
    }

    @Override
    public void decreaseStock(Receipt receipt, ReceiptType receiptType) {
        receipt.getRecords().forEach(receiptRecord -> decreaseStock(receiptRecord, receipt.getType()));
    }

    @Override
    public void decreaseStock(ReceiptRecord receiptRecord, ReceiptType receiptType) {
        List<Recipe> recipes = recipeRepository.findAllByOwner(receiptRecord.getProduct());
        recipes.forEach(recipe -> {
            Stock stock = getLatestItemByProduct(recipe.getComponent());
            double quantity = calculateStockQuantity(receiptRecord, receiptType, recipe);
            decreaseStock(stock, roundToTwoDecimals(quantity), receiptType);
            stockRepository.save(stock);
        });
    }

    private Double calculateStockQuantity(ReceiptRecord receiptRecord, ReceiptType receiptType, Recipe recipe) {
        return Optional.of(receiptType).filter(type -> type.equals(ReceiptType.SALE))
                .map(type -> recipe.getQuantityMultiplier() * receiptRecord.getSoldQuantity())
                .orElse(receiptRecord.getAbsoluteQuantity());
    }

    @Override
    public void closeLatestStockEntries() {
        productService.getStorableProducts().forEach(productAdapter -> {
                    Stock stock = getLatestItemByProduct(productAdapter);
                    if (isStockChanged(stock)) {
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
        if (ReceiptType.isSale(type)) {
            increaseSoldQuantity(stock, quantity);
        } else if (ReceiptType.isPurchase(type)) {
            increasePurchasedQuantity(stock, quantity);
        } else if (ReceiptType.isInventory(type)) {
            increaseInventoryQuantity(stock, quantity);
        } else if (ReceiptType.isDisposal(type)) {
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
        if (ReceiptType.isSale(type)) {
            decreaseSoldQuantity(stock, quantity);
        } else {
            // ONLY SALE RECEIPTS CAN BE REOPENED.
        }
    }

    private void decreaseSoldQuantity(Stock stock, double quantity) {
        stock.setSoldQuantity(stock.getSoldQuantity() - quantity / stock.getOwner().getStorageMultiplier());
        stockRepository.save(stock);
    }
}
