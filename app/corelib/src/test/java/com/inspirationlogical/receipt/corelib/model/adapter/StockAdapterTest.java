package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bálint on 2017.04.04..
 */
public class StockAdapterTest {

    private ProductAdapter productTwo;
    private ProductAdapter recipeElementOne;
    private StockAdapter stockProductTwo;
    private StockAdapter stockRecipeElementOne;
    private ReceiptRecordAdapter receiptSaleTwo;
    private double stockInitialSoldQuantity;
    private double stockInitialPurchasedQuantity;
    private double stockInitialInitialQuantity;
    private double storageMultiplier;
    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        productTwo = new ProductAdapter(schema.getProductTwo());
        stockProductTwo = StockAdapter.getLatestItemByProduct(productTwo);
        recipeElementOne = new ProductAdapter(schema.getProductRecipeElementOne());
        stockRecipeElementOne = StockAdapter.getLatestItemByProduct(recipeElementOne);
        receiptSaleTwo = new ReceiptRecordAdapter(schema.getReceiptRecordSaleTwo());
        stockInitialSoldQuantity = stockRecipeElementOne.getAdaptee().getSoldQuantity();
        stockInitialPurchasedQuantity = stockRecipeElementOne.getAdaptee().getPurchasedQuantity();
        stockInitialInitialQuantity = stockRecipeElementOne.getAdaptee().getInitialQuantity();
        storageMultiplier = stockRecipeElementOne.getAdaptee().getOwner().getStorageMultiplier();
    }
    @Test
    public void testGetLatestItemByProduct() {
        assertEquals(10, stockRecipeElementOne.getAdaptee().getInitialQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockSale() {
        stockProductTwo.updateStock(receiptSaleTwo, ReceiptType.SALE);
        // 0.5 is the quantityMultiplier
        assertEquals(receiptSaleTwo.getAdaptee().getSoldQuantity() * 0.5 / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getSoldQuantity(), 0.001);
        assertEquals(0, stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockPurchase() {
        stockProductTwo.updateStock(receiptSaleTwo, ReceiptType.PURCHASE);
        assertEquals(0, stockProductTwo.getAdaptee().getSoldQuantity(), 0.001);
        assertEquals(receiptSaleTwo.getAdaptee().getSoldQuantity() / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockDisposal() {
        stockProductTwo.updateStock(receiptSaleTwo, ReceiptType.DISPOSAL);
        assertEquals(receiptSaleTwo.getAdaptee().getAbsoluteQuantity() / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getSoldQuantity(), 0.001);
        assertEquals(0, stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockInventoryPositive() {
        stockProductTwo.updateStock(receiptSaleTwo, ReceiptType.INVENTORY);
        assertEquals(receiptSaleTwo.getAdaptee().getSoldQuantity() / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getSoldQuantity(), 0.001);
        assertEquals(0, stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockInventoryNegative() {
        receiptSaleTwo.getAdaptee().setAbsoluteQuantity(-2);
        stockProductTwo.updateStock(receiptSaleTwo, ReceiptType.INVENTORY);
        assertEquals(0, stockProductTwo.getAdaptee().getSoldQuantity(), 0.001);
        assertEquals(receiptSaleTwo.getAdaptee().getSoldQuantity() / productTwo.getAdaptee().getStorageMultiplier(), stockProductTwo.getAdaptee().getPurchasedQuantity(), 0.001);
    }

    @Test
    public void testUpdateStockAdapterSale() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.SALE);
        assertEquals(5 / storageMultiplier + stockInitialSoldQuantity, stockRecipeElementOne.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterDisposal() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.DISPOSAL);
        assertEquals(5 / storageMultiplier + stockInitialSoldQuantity, stockRecipeElementOne.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterPurchase() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.PURCHASE);
        assertEquals(5 / storageMultiplier + stockInitialPurchasedQuantity, stockRecipeElementOne.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterInventoryNegative() {
        stockRecipeElementOne.updateStockAdapter(-5, ReceiptType.INVENTORY);
        assertEquals(5 / storageMultiplier + stockInitialPurchasedQuantity, stockRecipeElementOne.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterInventoryPositive() {
        stockRecipeElementOne.updateStockAdapter(5, ReceiptType.INVENTORY);
        assertEquals(5 / storageMultiplier + stockInitialSoldQuantity, stockRecipeElementOne.getAdaptee().getSoldQuantity(), 0.01);
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
