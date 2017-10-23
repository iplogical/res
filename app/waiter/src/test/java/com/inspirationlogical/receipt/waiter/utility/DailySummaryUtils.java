package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.getLabel;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.PayUtils.pay;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterSaleView;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.openTable;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.assertSoldProduct;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.selectCategory;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.sellProduct;
import static org.junit.Assert.assertEquals;

public class DailySummaryUtils {

    public static final int INIT_TOTAL_CASH = 5600;
    public static final int INIT_TOTAL_CREDITCARD = 2000;
    public static final int INIT_TOTAL_COUPON = 0;

    public static void backToRestaurantView() {
        clickButtonThenWait("Common.BackToRestaurantView", 1000);
    }

    public static int getOpenConsumption() {
        return Integer.valueOf(getLabel(DAILY_SUMMARY_OPEN_CONSUMPTION));
    }

    public static int getCashTotalPrice() {
        return Integer.valueOf(getLabel(DAILY_SUMMARY_CASH_TOTAL_PRICE));
    }

    public static int getCreditCardTotalPrice() {
        return Integer.valueOf(getLabel(DAILY_SUMMARY_CREDIT_CARD_TOTAL_PRICE));
    }

    public static int getCouponTotalPrice() {
        return Integer.valueOf(getLabel(DAILY_SUMMARY_COUPON_TOTAL_PRICE));
    }

    public static int getTotalPrice() {
        return Integer.valueOf(getLabel(DAILY_SUMMARY_TOTAL_PRICE));
    }

    public static void payByMethod(PaymentMethod paymentMethod) {
        clickButtonThenWait(TO_PAYMENT, 200);
        if(paymentMethod.equals(PaymentMethod.CASH)) {
            clickButtonThenWait(PAYMENT_METHOD_CASH, 50);
        } else if(paymentMethod.equals(PaymentMethod.CREDIT_CARD)) {
            clickButtonThenWait(PAYMENT_METHOD_CREDIT_CARD, 50);
        } else if(paymentMethod.equals(PaymentMethod.COUPON)) {
            clickButtonThenWait(PAYMENT_METHOD_COUPON, 50);
        }
        pay();
    }

    public static void assertSoldTotalPrices(int cash, int creditCard, int coupon) {
        assertEquals(13100, getOpenConsumption());
        assertEquals(5600 + cash, getCashTotalPrice());
        assertEquals(2000 + creditCard, getCreditCardTotalPrice());
        assertEquals(0 + coupon, getCouponTotalPrice());
        assertEquals(20700 + cash + creditCard + coupon, getTotalPrice());
    }

    public static void openTableAndSellProducts() {
        openTable("21");
        enterSaleView("21");
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE, 5);
        selectCategory(AGGREGATE_TWO);
        sellProduct(PRODUCT_THREE, 4);
    }

    public static void assertTotalCash(int deltaRow, int deltaCash) {
        assertSoldProduct(5 + deltaRow, PaymentMethod.CASH.toI18nString(), 0, INIT_TOTAL_CASH + deltaCash, 0);
    }

    public static void assertTotalCreditCard(int deltaRow, int deltaCreditCard) {
        assertSoldProduct(6 + deltaRow, PaymentMethod.CREDIT_CARD.toI18nString(), 0, INIT_TOTAL_CREDITCARD + deltaCreditCard, 0);
    }

    public static void assertTotalCoupon(int deltaRow, int deltaCoupon) {
        assertSoldProduct(7 + deltaRow, PaymentMethod.COUPON.toI18nString(), 0, INIT_TOTAL_COUPON + deltaCoupon, 0);
    }
}
