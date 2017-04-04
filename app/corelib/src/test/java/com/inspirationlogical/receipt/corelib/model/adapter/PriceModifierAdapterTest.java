package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bálint on 2017.04.04..
 */
public class PriceModifierAdapterTest {

    ReceiptRecordAdapter receiptRecordAdapter;
    PriceModifierAdapter simpleDiscount;
    PriceModifierAdapter quantityDiscount;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        receiptRecordAdapter = new ReceiptRecordAdapter(schema.getReceiptRecordSaleTwo());
        simpleDiscount = new PriceModifierAdapter(schema.getPriceModifierTwo());
        quantityDiscount = new PriceModifierAdapter(schema.getPriceModifierFour());
    }

    @Test
    public void testGetSimpleDiscount() {
        assertEquals(33.333, simpleDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
    }

    @Test
    public void testGetQuantityDiscount2OutOf3() {
        assertEquals(0, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
    }

    @Test
    public void testGetQuantityDiscount3OutOf3() {
        receiptRecordAdapter.getAdaptee().setSoldQuantity(3);
        assertEquals(33.333, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
    }

    @Test
    public void testGetQuantityDiscount4OutOf3() {
        receiptRecordAdapter.getAdaptee().setSoldQuantity(4);
        assertEquals(25, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
    }

    @Test
    public void testGetQuantityDiscount5OutOf3() {
        receiptRecordAdapter.getAdaptee().setSoldQuantity(5);
        assertEquals(20, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
    }

    @Test
    public void testGetQuantityDiscount6OutOf3() {
        receiptRecordAdapter.getAdaptee().setSoldQuantity(6);
        assertEquals(33.333, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
    }

    @Test
    public void testGetQuantityDiscount7OutOf3() {
        receiptRecordAdapter.getAdaptee().setSoldQuantity(7);
        assertEquals(28.571, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
    }
}
