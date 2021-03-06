package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.TestBase;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureAdapterTest extends TestBase {

//    private DailyClosureServiceImpl openDailyClosure;
//    private DailyClosureServiceImpl dailyClosureOne;
//    private DailyClosureServiceImpl dailyClosureTwo;
//    private Receipt receipt;
//
//    @Before
//    public void setUp() {
//        openDailyClosure = new DailyClosureServiceImpl(schema.getOpenDailyClosure());
//        dailyClosureOne = new DailyClosureServiceImpl(schema.getDailyClosureOne());
//        dailyClosureTwo = new DailyClosureServiceImpl(schema.getDailyClosureTwo());
//        receipt = schema.getReceiptSaleOne();
//    }
//
//    @Test
//    public void testGetLatestClosureTime() {
//        assertNotNull(DailyClosureServiceImpl.getLatestClosureTime());
//    }
//
//    @Test
//    public void testGetClosureTimesBefore() {
//        assertEquals(dailyClosureOne.getAdaptee().getClosureTime(), DailyClosureServiceImpl.getClosureTimes(now(), now()).get(0));
//        assertEquals(dailyClosureTwo.getAdaptee().getClosureTime(), DailyClosureServiceImpl.getClosureTimes(now().minusDays(1), now()).get(0));
//    }
//
//    @Test
//    public void testGetClosureTimesAfter() {
//        assertEquals(now().plusDays(1).atTime(21, 0), DailyClosureServiceImpl.getClosureTimes(now(), now()).get(1));
//        assertEquals(dailyClosureOne.getAdaptee().getClosureTime(), DailyClosureServiceImpl.getClosureTimes(now().minusDays(3), now().minusDays(2)).get(1));
//        assertEquals(dailyClosureTwo.getAdaptee().getClosureTime(), DailyClosureServiceImpl.getClosureTimes(now().minusDays(4), now().minusDays(3)).get(1));
//    }
//
//    @Test
//    public void testGetOpenDailyClosure() {
//        DailyClosureServiceImpl openDailyClosure = DailyClosureServiceImpl.getOpenDailyClosure();
//        assertNotNull(openDailyClosure);
//        assertNull(openDailyClosure.getAdaptee().getClosureTime());
//    }
//
//    @Test
//    public void testCloseDailyClosure() {
//        openDailyClosure.update(receipt);
//        openDailyClosure.closeDay();
//        assertEquals(15543, openDailyClosure.getAdaptee().getProfit());
//        assertEquals(75.09, openDailyClosure.getAdaptee().getMarkup(), 0.001);
//        assertEquals(20700, openDailyClosure.getAdaptee().getReceiptAverage());
//        assertEquals(1, openDailyClosure.getAdaptee().getNumberOfReceipts());
//        assertNotNull(DailyClosureServiceImpl.getOpenDailyClosure());
//    }
//
//    @Test
//    public void testUpdateDailyClosureCash() {
//        openDailyClosure.update(receipt);
//        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCash());
//        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCash());
//        assertEquals(receipt.getSumSaleGrossPrice() + OPEN_DAILY_CLOSURE_INIT_GROSS_CASH, openDailyClosure.getAdaptee().getSumSaleGrossPriceCash());
//        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCash());
//        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
//        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
//        assertEquals(receipt.getSumSaleGrossPrice() + OPEN_DAILY_CLOSURE_INIT_GROSS_TOTAL, openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
//        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
//    }
//
//    @Test
//    public void testUpdateDailyClosureCreditCard() {
//        receipt.setPaymentMethod(PaymentMethod.CREDIT_CARD);
//        openDailyClosure.update(receipt);
//        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCreditCard());
//        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCreditCard());
//        assertEquals(receipt.getSumSaleGrossPrice() + OPEN_DAILY_CLOSURE_INIT_GROSS_CREDIT_CARD, openDailyClosure.getAdaptee().getSumSaleGrossPriceCreditCard());
//        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCreditCard());
//        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
//        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
//        assertEquals(receipt.getSumSaleGrossPrice() + OPEN_DAILY_CLOSURE_INIT_GROSS_TOTAL, openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
//        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
//    }
//
//    @Test
//    public void testUpdateDailyClosureCoupon() {
//        receipt.setPaymentMethod(PaymentMethod.COUPON);
//        openDailyClosure.update(receipt);
//        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceCoupon());
//        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceCoupon());
//        assertEquals(receipt.getSumSaleGrossPrice() + OPEN_DAILY_CLOSURE_INIT_GROSS_COUPON, openDailyClosure.getAdaptee().getSumSaleGrossPriceCoupon());
//        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceCoupon());
//        assertEquals(receipt.getSumPurchaseGrossPrice(), openDailyClosure.getAdaptee().getSumPurchaseGrossPriceTotal());
//        assertEquals(receipt.getSumPurchaseNetPrice(), openDailyClosure.getAdaptee().getSumPurchaseNetPriceTotal());
//        assertEquals(receipt.getSumSaleGrossPrice() + OPEN_DAILY_CLOSURE_INIT_GROSS_TOTAL, openDailyClosure.getAdaptee().getSumSaleGrossPriceTotal());
//        assertEquals(receipt.getSumSaleNetPrice(), openDailyClosure.getAdaptee().getSumSaleNetPriceTotal());
//    }
}
