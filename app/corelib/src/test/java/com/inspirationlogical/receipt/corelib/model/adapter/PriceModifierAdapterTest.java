package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule.NUMBER_OF_PRICE_MODIFIERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Bálint on 2017.04.04..
 */
public class PriceModifierAdapterTest {

    private ReceiptRecordAdapter receiptRecordAdapter;
    private PriceModifierAdapter simpleDiscount;
    private PriceModifierAdapter quantityDiscount;
    private PriceModifierAdapter weeklyDiscount;
    private PriceModifierAdapter dailyDiscount;
    private PriceModifier.PriceModifierBuilder builder;

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Before
    public void setUp() {
        receiptRecordAdapter = new ReceiptRecordAdapter(schema.getReceiptSaleOneRecordTwo());
        simpleDiscount = new PriceModifierAdapter(schema.getPriceModifierTwo());
        quantityDiscount = new PriceModifierAdapter(schema.getPriceModifierFour());
        weeklyDiscount = new PriceModifierAdapter(schema.getPriceModifierFour());
        dailyDiscount = new PriceModifierAdapter(schema.getPriceModifierTwo());
        builder = PriceModifier.builder()
                .name("Test1")
                .type(PriceModifierType.QUANTITY_DISCOUNT)
                .repeatPeriod(PriceModifierRepeatPeriod.WEEKLY)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());
    }

    @Test
    public void testGetPriceModifiers() {
        assertEquals(NUMBER_OF_PRICE_MODIFIERS, PriceModifierAdapter.getPriceModifiers().size());
    }

    @Test
    public void testAddPriceModifierForProduct() {
        PriceModifierParams params = PriceModifierParams.builder()
                .originalName("Test1")
                .ownerName("product")
                .isCategory(false)
                .builder(builder)
                .build();
        PriceModifierAdapter.addPriceModifier(params);
        assertEquals(NUMBER_OF_PRICE_MODIFIERS + 1, PriceModifierAdapter.getPriceModifiers().size());
    }

    @Test
    public void testAddPriceModifierForCategory() {
        PriceModifierParams params = PriceModifierParams.builder()
                .originalName("Test1")
                .ownerName("leafOne")
                .isCategory(true)
                .builder(builder)
                .build();
        PriceModifierAdapter.addPriceModifier(params);
        assertEquals(NUMBER_OF_PRICE_MODIFIERS + 1, PriceModifierAdapter.getPriceModifiers().size());
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

    @Test
    public void testIsValidWeeklyTrue() {
        assertTrue(PriceModifierAdapter.isValidNow(weeklyDiscount));
    }

    @Test
    public void testIsValidWeeklyFalse() {
        weeklyDiscount.getAdaptee().setDayOfWeek(LocalDate.now().plusDays(1).getDayOfWeek());
        assertFalse(PriceModifierAdapter.isValidNow(weeklyDiscount));
    }

    @Test
    public void testIsValidDailyTrue() {
        assertTrue(PriceModifierAdapter.isValidNow(dailyDiscount));
    }

    @Test
    public void testIsValidDailyFalseByStart() {
        dailyDiscount.getAdaptee().setStartTime(LocalTime.now().plusMinutes(5));
        assertFalse(PriceModifierAdapter.isValidNow(dailyDiscount));
    }

    @Test
    public void testIsValidDailyFalseByEnd() {
        dailyDiscount.getAdaptee().setEndTime(LocalTime.now().minusMinutes(1));
        assertFalse(PriceModifierAdapter.isValidNow(dailyDiscount));
    }


    @Test
    public void testIsValidNoRepetition() {
        dailyDiscount.getAdaptee().setRepeatPeriod(PriceModifierRepeatPeriod.NO_REPETITION);
        assertTrue(PriceModifierAdapter.isValidNow(dailyDiscount));
    }
}
