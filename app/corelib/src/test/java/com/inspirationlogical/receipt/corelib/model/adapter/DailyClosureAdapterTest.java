package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
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
    private Receipt receipt;

    @Before
    public void setUp() {
        openDailyClosure = new DailyClosureAdapter(schema.getDailyClosureTwo());
        receipt = schema.getReceiptSaleOne();
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
        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCash());
        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCash());
        assertEquals(receipt.getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceCash());
        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCash());
        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
        assertEquals(receipt.getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
    }

    @Test
    public void testUpdateDailyClosureCreditCard() {
        receipt.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        openDailyClosure.update(receipt);
        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCreditCard());
        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCreditCard());
        assertEquals(receipt.getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceCreditCard());
        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCreditCard());
        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
        assertEquals(receipt.getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
    }

    @Test
    public void testUpdateDailyClosureCoupon() {
        receipt.setPaymentMethod(PaymentMethod.COUPON);
        openDailyClosure.update(receipt);
        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCoupon());
        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCoupon());
        assertEquals(receipt.getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceCoupon());
        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCoupon());
        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
        assertEquals(receipt.getSumSaleGrossPrice(), openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
    }
}
