package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PriceModifierAdapterTest extends TestBase {

//    private ReceiptRecordAdapter receiptRecordAdapter;
//    private PriceModifierAdapter simpleDiscount;
//    private PriceModifierAdapter quantityDiscount;
//    private PriceModifierAdapter weeklyDiscount;
//    private PriceModifierAdapter dailyDiscount;
//    private PriceModifier.PriceModifierBuilder builder;
//
//    @Before
//    public void setUp() {
//        receiptRecordAdapter = new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordTwo());
//        simpleDiscount = new PriceModifierAdapter(schema.getPriceModifierTwo());
//        quantityDiscount = new PriceModifierAdapter(schema.getPriceModifierFour());
//        weeklyDiscount = new PriceModifierAdapter(schema.getPriceModifierFour());
//        dailyDiscount = new PriceModifierAdapter(schema.getPriceModifierTwo());
//        builder = PriceModifier.builder()
//                .name("Test1")
//                .type(PriceModifierType.QUANTITY_DISCOUNT)
//                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
//                .startDate(LocalDateTime.now())
//                .endDate(LocalDateTime.now());
//    }
//
//    @Test
//    public void testGetPriceModifiers() {
//        assertEquals(NUMBER_OF_PRICE_MODIFIERS, PriceModifierAdapter.getPriceModifiers().size());
//    }
//
//    @Test
//    public void testAddPriceModifierForProduct() {
//        PriceModifierParams params = PriceModifierParams.builder()
//                .originalName("Test1")
//                .ownerName("productOne")
//                .isCategory(false)
//                .builder(builder)
//                .build();
//        PriceModifierAdapter.addPriceModifier(params);
//        assertEquals(NUMBER_OF_PRICE_MODIFIERS + 1, PriceModifierAdapter.getPriceModifiers().size());
//    }
//
//    @Test
//    public void testAddPriceModifierForCategory() {
//        PriceModifierParams params = PriceModifierParams.builder()
//                .originalName("Test1")
//                .ownerName("leafOne")
//                .isCategory(true)
//                .builder(builder)
//                .build();
//        PriceModifierAdapter.addPriceModifier(params);
//        assertEquals(NUMBER_OF_PRICE_MODIFIERS + 1, PriceModifierAdapter.getPriceModifiers().size());
//    }
//
//    @Test
//    public void testGetSimpleDiscount() {
//        assertEquals(33.333, simpleDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
//    }
//
//    @Test
//    public void testGetQuantityDiscount2OutOf3() {
//        assertEquals(0, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
//    }
//
//    @Test
//    public void testGetQuantityDiscount3OutOf3() {
//        receiptRecordAdapter.getAdaptee().setSoldQuantity(3);
//        assertEquals(33.333, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
//    }
//
//    @Test
//    public void testGetQuantityDiscount4OutOf3() {
//        receiptRecordAdapter.getAdaptee().setSoldQuantity(4);
//        assertEquals(25, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
//    }
//
//    @Test
//    public void testGetQuantityDiscount5OutOf3() {
//        receiptRecordAdapter.getAdaptee().setSoldQuantity(5);
//        assertEquals(20, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
//    }
//
//    @Test
//    public void testGetQuantityDiscount6OutOf3() {
//        receiptRecordAdapter.getAdaptee().setSoldQuantity(6);
//        assertEquals(33.333, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
//    }
//
//    @Test
//    public void testGetQuantityDiscount7OutOf3() {
//        receiptRecordAdapter.getAdaptee().setSoldQuantity(7);
//        assertEquals(28.571, quantityDiscount.getDiscountPercent(receiptRecordAdapter), 0.01);
//    }
//
//    @Test
//    public void testIsValidWeekly() {
//        LocalDateTime todayMorning = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(4, 0));
//        LocalDateTime tomorrowNight = LocalDateTime.of(LocalDateTime.now().plusDays(1).toLocalDate(), LocalTime.of(3, 59));
//        assertFalse(weeklyDiscount.isValidNow(todayMorning));
//        assertTrue(weeklyDiscount.isValidNow(todayMorning.plusMinutes(1)));
//        assertTrue(weeklyDiscount.isValidNow(tomorrowNight));
//        assertFalse(weeklyDiscount.isValidNow(tomorrowNight.plusMinutes(1)));
//    }
//
//    @Test
//    public void testIsValidDaily() {
//        LocalDateTime dailyStart = LocalDateTime.of(LocalDateTime.now().toLocalDate(), dailyDiscount.getAdaptee().getStartTime());
//        LocalDateTime dailyEnd = LocalDateTime.of(LocalDateTime.now().toLocalDate(), dailyDiscount.getAdaptee().getEndTime());
//        assertFalse(dailyDiscount.isValidNow(dailyStart.minusMinutes(1)));
//        assertTrue(dailyDiscount.isValidNow(dailyStart));
//        assertTrue(dailyDiscount.isValidNow(dailyStart.plusMinutes(30)));
//        assertTrue(dailyDiscount.isValidNow(dailyEnd));
//        assertFalse(dailyDiscount.isValidNow(dailyEnd.plusMinutes(1)));
//    }
}
