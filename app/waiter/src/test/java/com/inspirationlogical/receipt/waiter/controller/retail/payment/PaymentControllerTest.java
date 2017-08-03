package com.inspirationlogical.receipt.waiter.controller.retail.payment;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.PayUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
import static org.junit.Assert.assertEquals;

public class PaymentControllerTest  extends TestFXBase {

    private static final String TABLE_NAME = "TestName20";
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
        assertInitialState();

    }

    private void assertInitialState() {
        assertSoldSoproni(1, 3);
        assertSoldGere(2, 2);
        assertNumberOfSoldProducts(2);
        assertSoldTotalPrice(7120);

        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(0);
        assertNoPaidProduct();
    }

    @Test
    public void testSelectivePayment() {
        paySelective(1);
        assertPaidSoproni(1, 3);
        assertPaidTotalPrice(1320);
        assertPreviousPartialPrice(0);
        pay();

        assertSoldGere(1, 2);
        assertNumberOfSoldProducts(1);
        assertNoPaidProduct();
        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(1320);
    }

    @Test
    public void testSinglePayment() {
        paySingle(1);
        testSinglePaymentSoldProductsAssertions$$1();
        testSinglePaymentPaidProductsAssertions$$1();
        paySingle(1);
        testSinglePaymentSoldProductsAssertions$$2();
        testSinglePaymentPaidProductsAssertions$$2();
        pay();
        testSinglePaymentSoldProductsAssertions$$2();
        testSinglePaymentPaidProductsAssertionsAfterPay$$1();
    }

    private void testSinglePaymentSoldProductsAssertions$$1() {
        assertSoldSoproni(1, 2);
        assertSoldGere(2, 2);
        assertSoldTotalPrice(6680);
    }

    private void testSinglePaymentPaidProductsAssertions$$1() {
        assertPaidSoproni(1,1);
        assertPaidTotalPrice(440);
        assertPreviousPartialPrice(0);
    }

    private void testSinglePaymentSoldProductsAssertions$$2() {
        assertSoldSoproni(1, 1);
        assertSoldGere(2, 2);
        assertSoldTotalPrice(6240);

    }

    private void testSinglePaymentPaidProductsAssertions$$2() {
        assertPaidSoproni(1,2);
        assertPaidTotalPrice(880);
        assertPreviousPartialPrice(0);
    }

    private void testSinglePaymentPaidProductsAssertionsAfterPay$$1() {
        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(880);
        assertNoPaidProduct();
    }

    @Test
    public void testSinglePaymentPayAllOfTheRow() {
        paySingle(1);
        testSinglePaymentSoldProductsAssertions$$1();
        testSinglePaymentPaidProductsAssertions$$1();
        paySingle(1);
        testSinglePaymentSoldProductsAssertions$$2();
        testSinglePaymentPaidProductsAssertions$$2();
        paySingle(1);
        testSinglePaymentSoldProductsAssertions$$3();
        testSinglePaymentPaidProductsAssertions$$3();
        pay();
        testSinglePaymentPaidProductsAssertionsAfterPay$$2();
    }

    @Test
    public void testPartialPaymentNotAllowed() {
        payPartial(1, 0.5);
        assertSoldSoproni(1, 3);
        assertSoldGere(2, 2);
        assertNoPaidProduct();
    }

    @Test
    public void testPartialPaymentIllegalInput() {
        setTextField(PARTIAL_PAYMENT_VALUE, "NotDouble");
        payPartial(2);
        verifyThatVisible(Resources.WAITER.getString("PaymentView.PartialPayNumberError"));
        assertSoldSoproni(1, 3);
        assertSoldGere(2, 2);
        assertNoPaidProduct();
    }

    private void testSinglePaymentSoldProductsAssertions$$3() {
        assertSoldGere(1, 2);
        assertSoldTotalPrice(5800);
    }

    private void testSinglePaymentPaidProductsAssertions$$3() {
        assertPaidSoproni(1, 3);
        assertPaidTotalPrice(1320);
        assertPreviousPartialPrice(0);
    }

    private void testSinglePaymentPaidProductsAssertionsAfterPay$$2() {
        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(1320);
        assertNoPaidProduct();
    }

    @Test
    public void testPartialPayment() {
        payPartial(2, 0.5);
        assertSoldSoproni(1, 3);
        assertSoldGere(2, 1.5);
        assertSoldTotalPrice(5670);
        assertPaidGere(1, 0.5);
        assertPaidTotalPrice(1450);
        assertPreviousPartialPrice(0);
        pay();
    }

    @Test
    public void testPartialPaymentAllOfTheRow() {
        System.out.println("In the test: " + Thread.currentThread().getName());
        payPartialOneAndHalf();
        pay();
        payPartial(2, 0.5);
        assertSoldSoproni(1, 3);
        assertSoldTotalPrice(1320);
        assertPaidGere(1, 0.5);
        assertPaidTotalPrice(1450);
        assertPreviousPartialPrice(4350);
        pay();
    }

    private void payPartialOneAndHalf() {
        payPartial(2, 1.5);
        assertSoldSoproni(1, 3);
        assertSoldGere(2, 0.5);
        assertSoldTotalPrice(2770);
        assertPaidGere(1, 1.5);
        assertPaidTotalPrice(4350);
        assertPreviousPartialPrice(0);
    }

    @Test
    public void testSinglePaymentPartialValue() {
        payPartialOneAndHalf();
        pay();
        paySingle(2);
        assertSoldSoproni(1, 3);
        assertSoldTotalPrice(1320);
        assertPaidGere(1, 0.5);
        assertPaidTotalPrice(1450);
        assertPreviousPartialPrice(4350);
        pay();
    }

    @Test
    public void testMovePaidProductBackToSoldProducts() {
        paySingle(1);
        assertSoldSoproni(1, 2);
        assertSoldTotalPrice(6680);
        assertPaidSoproni(1, 1);
        assertPaidTotalPrice(440);

        paySingle(1);
        assertSoldSoproni(1, 1);
        assertSoldTotalPrice(6240);
        assertPaidSoproni(1, 2);
        assertPaidTotalPrice(880);

        paySingle(1);
        assertSoldGere(1, 2);
        assertNumberOfSoldProducts(1);
        assertSoldTotalPrice(5800);
        assertPaidSoproni(1, 3);
        assertPaidTotalPrice(1320);

        putBackToSold(1);
        assertSoldSoproni(2, 1);
        assertSoldTotalPrice(6240);
        assertPaidSoproni(1, 2);
        assertPaidTotalPrice(880);

        putBackToSold(1);
        assertSoldSoproni(2, 2);
        assertSoldTotalPrice(6680);
        assertPaidSoproni(1, 1);
        assertPaidTotalPrice(440);

        putBackToSold(1);
        assertSoldSoproni(2, 3);
        assertSoldTotalPrice(7120);
        assertNoPaidProduct();
        assertPaidTotalPrice(0);

        paySelective(2);
        assertSoldGere(1, 2);
        assertNumberOfSoldProducts(1);
        assertSoldTotalPrice(5800);
        assertPaidSoproni(1, 3);
        assertPaidTotalPrice(1320);

        pay();
        assertNoPaidProduct();
        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(1320);
    }

    @After
    public void payTable() {
        clickButtonThenWait(PAY, 2000);
    }
}
