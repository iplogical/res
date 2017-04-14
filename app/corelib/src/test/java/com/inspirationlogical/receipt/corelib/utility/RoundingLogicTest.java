package com.inspirationlogical.receipt.corelib.utility;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Ferenc on 2017. 04. 14..
 */
public class RoundingLogicTest {

    @Test
    public void test_if_paymentmethod_is_not_cash_no_rounding_is_applied() {
        RoundingLogic r_coupon = RoundingLogic.create(PaymentMethod.COUPON);
        RoundingLogic r_card = RoundingLogic.create(PaymentMethod.CREDIT_CARD);
        assertEquals(3.13, r_coupon.round(3.13));
        assertEquals(3.13, r_card.round(3.13));
        assertEquals(4123.13, r_coupon.round(4123.13));
        assertEquals(569.13, r_card.round(569.13));
    }

    @Test
    public void test_if_paymentmethod_is_cash_rounding_is_applied() {
        RoundingLogic r_cash = RoundingLogic.create(PaymentMethod.CASH);
        assertEquals(410, (int)r_cash.round(412));
        assertEquals(415, (int)r_cash.round(413));
        assertEquals(415, (int)r_cash.round(417));
        assertEquals(420, (int)r_cash.round(418));
    }

}
