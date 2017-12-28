package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter.decreaseStock;
import static com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter.updateStock;
import static org.junit.Assert.assertEquals;

/**
 * Created by Bálint on 2017.04.04..
 */
public class StockAdapterTest extends TestBase {

    private ProductAdapter productTwo;
    private ProductAdapter recipeElementOne;
    private StockAdapter stockProductTwo;
    private StockAdapter stockRecipeElementOne;
    private ReceiptRecord receiptSaleTwo;
    private double stockInitialSoldQuantity;
    private double stockInitialPurchasedQuantity;
    private double stockInitialInventoryQuantity;
    private double stockInitialDisposedQuantity;
    private double stockInitialInitialQuantity;
    private double storageMultiplier;

    @Before
    public void setUp() {
        productTwo = new ProductAdapter(schema.getProductTwo());
        stockProductTwo = StockAdapter.getLatestItemByProduct(productTwo);
        recipeElementOne = new ProductAdapter(schema.getProductRecipeElementOne());
        stockRecipeElementOne = StockAdapter.getLatestItemByProduct(recipeElementOne);
        receiptSaleTwo = schema.getReceiptSaleOneRecordTwo();
        stockInitialSoldQuantity = stockRecipeElementOne.getAdaptee().getSoldQuantity();
        stockInitialPurchasedQuantity = stockRecipeElementOne.getAdaptee().getPurchasedQuantity();
        stockInitialInventoryQuantity = stockRecipeElementOne.getAdaptee().getInventoryQuantity();
        stockInitialDisposedQuantity = stockRecipeElementOne.getAdaptee().getDisposedQuantity();
        stockInitialInitialQuantity = stockRecipeElementOne.getAdaptee().getInitialQuantity();
        storageMultiplier = stockRecipeElementOne.getAdaptee().getOwner().getStorageMultiplier();
    }
    @Test
    public void testGetLatestItemByProduct() {
        assertEquals(10, stockRecipeElementOne.getAdaptee().getInitialQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockSale() {
        updateStock(receiptSaleTwo, ReceiptType.SALE);
        // 0.5 is the quantityMultiplier
        assertEquals(receiptSaleTwo.getSoldQuantity() * 0.5 / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getSoldQuantity(), 0.001);
        assertEquals(0, stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testDecreaseStockSale() {
        decreaseStock(receiptSaleTwo, ReceiptType.SALE);
        // 0.5 is the quantityMultiplier
        assertEquals(receiptSaleTwo.getSoldQuantity() * 0.5 / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getSoldQuantity() * (-1), 0.001);
        assertEquals(0, stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockPurchase() {
        updateStock(receiptSaleTwo, ReceiptType.PURCHASE);
        assertEquals(0, stockProductTwo.getAdaptee().getSoldQuantity(), 0.001);
        assertEquals(receiptSaleTwo.getSoldQuantity() / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockDisposal() {
        updateStock(receiptSaleTwo, ReceiptType.DISPOSAL);
        assertEquals(receiptSaleTwo.getAbsoluteQuantity() / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getDisposedQuantity(), 0.001);
        assertEquals(0, stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockInventoryPositive() {
        updateStock(receiptSaleTwo, ReceiptType.INVENTORY);
        assertEquals(receiptSaleTwo.getSoldQuantity() / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getInventoryQuantity(), 0.001);
        assertEquals(0, stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockInventoryNegative() {
        receiptSaleTwo.setAbsoluteQuantity(-2);
        updateStock(receiptSaleTwo, ReceiptType.INVENTORY);
        assertEquals(0, stockProductTwo.getAdaptee().getSoldQuantity(), 0.001);
        assertEquals((receiptSaleTwo.getSoldQuantity() / productTwo.getAdaptee().getStorageMultiplier()) * -1, stockProductTwo.getAdaptee().getInventoryQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockAdapterSale() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.SALE);
        assertEquals(5 / storageMultiplier + stockInitialSoldQuantity, stockRecipeElementOne.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterDisposal() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.DISPOSAL);
        assertEquals(5 / storageMultiplier + stockInitialDisposedQuantity, stockRecipeElementOne.getAdaptee().getDisposedQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterPurchase() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.PURCHASE);
        assertEquals(5 / storageMultiplier + stockInitialPurchasedQuantity, stockRecipeElementOne.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterInventoryNegative() {
        stockRecipeElementOne.updateStockAdapter(-5, ReceiptType.INVENTORY);
        assertEquals((5 / storageMultiplier + stockInitialInventoryQuantity) * -1, stockRecipeElementOne.getAdaptee().getInventoryQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterInventoryPositive() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.INVENTORY);
        assertEquals(5 / storageMultiplier + stockInitialInventoryQuantity, stockRecipeElementOne.getAdaptee().getInventoryQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterOther() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.OTHER);
        assertEquals(stockInitialSoldQuantity, stockRecipeElementOne.getAdaptee().getSoldQuantity(), 0.01);
        assertEquals(stockInitialPurchasedQuantity, stockRecipeElementOne.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testCloseLatestStockEntries() {
        StockAdapter.closeLatestStockEntries();
        stockRecipeElementOne = StockAdapter.getLatestItemByProduct(recipeElementOne);
        assertEquals(stockInitialInitialQuantity - stockInitialSoldQuantity + stockInitialPurchasedQuantity, stockRecipeElementOne.getAdaptee().getInitialQuantity(), 0.01);
    }
}
