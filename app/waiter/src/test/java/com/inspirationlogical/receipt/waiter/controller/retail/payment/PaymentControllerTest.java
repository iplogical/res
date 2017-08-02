package com.inspirationlogical.receipt.waiter.controller.retail.payment;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickOnThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.openTable;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.PAY;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.SELECTIVE_PAYMENT;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.TO_PAYMENT;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;

public class PaymentControllerTest  extends TestFXBase {

    private static final String TABLE_NUMBER = "20";
    private static final String TABLE_GUESTS = "0";
    private static final String TABLE_CAPACITY = "4";

    @Before
    public void setUpPaymentControllerTest() throws Exception {
//        addTable(TABLE_NAME, TABLE_NUMBER, TABLE_GUESTS, TABLE_CAPACITY);
        openTable(TABLE_NUMBER);
        clickOnThenWait(TABLE_NUMBER, 200);
        selectCategory(BEERS);
        sellProduct(SOPRONI, 3);
        selectCategory(WINE);
        sellProduct(GERE, 2);
        clickButtonThenWait(TO_PAYMENT, 200);
    }

    @Test
    public void testSelectivePayment() {
        clickButtonThenWait(SELECTIVE_PAYMENT, 100);
        clickButtonThenWait(SELECTIVE_PAYMENT, 100);
    }

    @After
    public void payTable() {
        clickButtonThenWait(PAY, 2000);
    }

}
