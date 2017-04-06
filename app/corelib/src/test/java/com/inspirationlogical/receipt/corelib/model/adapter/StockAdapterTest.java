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
    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        productAdapter = new ProductAdapter(schema.getProductFour());
        stock = StockAdapter.getLatestItemByProduct(productAdapter);
    }
    @Test
    public void testGetLatestItemByProduct() {
        assertEquals(20, stock.getAdaptee().getInitialQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterSale() {
        stock.updateStockAdapter(5, ReceiptType.SALE);
        assertEquals(5, stock.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterDisposal() {
        stock.updateStockAdapter(5, ReceiptType.DISPOSAL);
        assertEquals(5, stock.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterPurchase() {
        stock.updateStockAdapter(5, ReceiptType.PURCHASE);
        assertEquals(5, stock.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterInventoryNegative() {
        stock.updateStockAdapter(-5, ReceiptType.INVENTORY);
        assertEquals(5, stock.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterInventoryPositive() {
        stock.updateStockAdapter(5, ReceiptType.INVENTORY);
        assertEquals(5, stock.getAdaptee().getSoldQuantity(), 0.01);
    }

    @Test
    public void testUpdateStockAdapterOther() {
        stock.updateStockAdapter(5, ReceiptType.OTHER);
        assertEquals(0, stock.getAdaptee().getSoldQuantity(), 0.01);
        assertEquals(0, stock.getAdaptee().getPurchasedQuantity(), 0.01);
    }

    @Test
    public void testCloseLatestStockEntries() {
        StockAdapter.closeLatestStockEntries();
        stock = StockAdapter.getLatestItemByProduct(productAdapter);
        assertEquals(-20, stock.getAdaptee().getInitialQuantity(), 0.01);
    }
}
