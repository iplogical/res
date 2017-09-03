package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureAdapterTest extends TestBase {

    private DailyClosureAdapter openDailyClosure;
    private ReceiptAdapterBase receipt;

    @Before
    public void setUp() {
        openDailyClosure = new DailyClosureAdapter(schema.getDailyClosureTwo());
        receipt = new ReceiptAdapterBase(schema.getReceiptSaleOne());
    }

    @Test
    public void testGetLatestClosureTime() {
        assertNotNull(DailyClosureAdapter.getLatestClosureTime());
    }

    @Test
    public void testGetOpenDailyClosure() {
        DailyClosureAdapter openDailyClosure = DailyClosureAdapter.getOpenDailyClosure();
        assertNotNull(openDailyClosure);
        assertNull(openDailyClosure.getAdaptee().getClosureTime());
    }

    @Test
    public void testCloseDailyClosure() {
        openDailyClosure.update(receipt);
        openDailyClosure.close();
        assertEquals(7943, openDailyClosure.getAdaptee().getProfit());
        assertEquals(60.63, openDailyClosure.getAdaptee().getMarkup(), 0.001);
        assertEquals(13100, openDailyClosure.getAdaptee().getReceiptAverage());
        assertEquals(1, openDailyClosure.getAdaptee().getNumberOfReceipts());
        assertNotNull(DailyClosureAdapter.getOpenDailyClosure());
    }
    
    @Test
    public void testUpdateDailyClosureCash() {
        openDailyClosure.update(receipt);
        assertEquals(receipt.getAdaptee().getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCash());
        assertEquals(receipt.getAdaptee().getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCash());
        assertEquals(receipt.getAdaptee().getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceCash());
        assertEquals(receipt.getAdaptee().getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCash());
        assertEquals(receipt.getAdaptee().getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
        assertEquals(receipt.getAdaptee().getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
        assertEquals(receipt.getAdaptee().getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
        assertEquals(receipt.getAdaptee().getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
    }

    @Test
    public void testUpdateDailyClosureCreditCard() {
        receipt.getAdaptee().setPaymentMethod(PaymentMethod.CREDIT_CARD);
        openDailyClosure.update(receipt);
        assertEquals(receipt.getAdaptee().getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCreditCard());
        assertEquals(receipt.getAdaptee().getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCreditCard());
        assertEquals(receipt.getAdaptee().getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceCreditCard());
        assertEquals(receipt.getAdaptee().getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCreditCard());
        assertEquals(receipt.getAdaptee().getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
        assertEquals(receipt.getAdaptee().getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
        assertEquals(receipt.getAdaptee().getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
        assertEquals(receipt.getAdaptee().getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
    }

    @Test
    public void testUpdateDailyClosureCoupon() {
        receipt.getAdaptee().setPaymentMethod(PaymentMethod.COUPON);
        openDailyClosure.update(receipt);
        assertEquals(receipt.getAdaptee().getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCoupon());
        assertEquals(receipt.getAdaptee().getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCoupon());
        assertEquals(receipt.getAdaptee().getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceCoupon());
        assertEquals(receipt.getAdaptee().getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCoupon());
        assertEquals(receipt.getAdaptee().getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
        assertEquals(receipt.getAdaptee().getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
        assertEquals(receipt.getAdaptee().getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
        assertEquals(receipt.getAdaptee().getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
    }
}
