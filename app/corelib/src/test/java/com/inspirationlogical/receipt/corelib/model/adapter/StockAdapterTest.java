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

    private ProductAdapter productAdapter;
    private StockAdapter stock;
    private double stockInitialSoldQuantity;
    private double stockInitialPurchasedQuantity;
    private double stockInitialInitialQuantity;
    private double storageMultiplier;
    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        productAdapter = new ProductAdapter(schema.getProductRecipeElementOne());
        stock = StockAdapter.getLatestItemByProduct(productAdapter);
        stockInitialSoldQuantity = stock.getAdaptee().getSoldQuantity();
        stockInitialPurchasedQuantity = stock.getAdaptee().getPurchasedQuantity();
        stockInitialInitialQuantity = stock.getAdaptee().getInitialQuantity();
        storageMultiplier = stock.getAdaptee().getOwner().getStorageMultiplier();
    }
    @Test
    public void testGetLatestItemByProduct() {
        assertEquals(10, stock.getAdaptee().getInitialQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterSale() {
        stock.updateStockAdapter(5, ReceiptType.SALE);
        assertEquals(5 / storageMultiplier + stockInitialSoldQuantity, stock.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterDisposal() {
        stock.updateStockAdapter(5, ReceiptType.DISPOSAL);
        assertEquals(5 / storageMultiplier + stockInitialSoldQuantity, stock.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterPurchase() {
        stock.updateStockAdapter(5, ReceiptType.PURCHASE);
        assertEquals(5 / storageMultiplier + stockInitialPurchasedQuantity, stock.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterInventoryNegative() {
        stock.updateStockAdapter(-5, ReceiptType.INVENTORY);
        assertEquals(5 / storageMultiplier + stockInitialPurchasedQuantity, stock.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterInventoryPositive() {
        stock.updateStockAdapter(5, ReceiptType.INVENTORY);
        assertEquals(5 / storageMultiplier + stockInitialSoldQuantity, stock.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterOther() {
        stock.updateStockAdapter(5, ReceiptType.OTHER);
        assertEquals(stockInitialSoldQuantity, stock.getAdaptee().getSoldQuantity(), 0.01);
        assertEquals(stockInitialPurchasedQuantity, stock.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testCloseLatestStockEntries() {
        StockAdapter.closeLatestStockEntries();
        stock = StockAdapter.getLatestItemByProduct(productAdapter);
        assertEquals(stockInitialInitialQuantity - stockInitialSoldQuantity + stockInitialPurchasedQuantity, stock.getAdaptee().getInitialQuantity(), 0.01);
    }
}
