package com.inspirationlogical.receipt.waiter.controller.retail.payment;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.PAYMENT_TEST_TABLE;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.PayUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.enterSaleView;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.openTable;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;
import static org.junit.Assert.assertEquals;

public class PaymentControllerTest  extends TestFXBase {

    private static final String TABLE_NUMBER = PAYMENT_TEST_TABLE;

    @Before
    public void setUpPaymentControllerTest() throws Exception {
        openTable(TABLE_NUMBER);
        clickOnThenWait(TABLE_NUMBER, 200);
        selectCategory(AGGREGATE_ONE);
        sellProduct(PRODUCT_FIVE, 3);
        selectCategory(AGGREGATE_TWO);
        sellProduct(PRODUCT_THREE, 2);
        clickButtonThenWait(TO_PAYMENT, 200);
        assertInitialState();
    }

    private void assertInitialState() {
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 2);
        assertNumberOfSoldProducts(2);
        assertSoldTotalPrice(7120);

        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(0);
        assertNoPaidProduct();
    }

    @Test
    public void testSelectivePayment() {
        paySelective(1);
        assertPaidProductFive(1, 3);
        assertPaidTotalPrice(1320);
        assertPreviousPartialPrice(0);
        pay();

        assertSoldProductThree(1, 2);
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
        clickOnThenWait(SINGLE_PAYMENT, 100);
        pay();
        clickOnThenWait(SINGLE_PAYMENT, 100);
        testSinglePaymentSoldProductsAssertions$$2();
        testSinglePaymentPaidProductsAssertionsAfterPay$$1();
    }

    private void testSinglePaymentSoldProductsAssertions$$1() {
        assertSoldProductFive(1, 2);
        assertSoldProductThree(2, 2);
        assertSoldTotalPrice(6680);
    }

    private void testSinglePaymentPaidProductsAssertions$$1() {
        assertPaidProductFive(1,1);
        assertPaidTotalPrice(440);
        assertPreviousPartialPrice(0);
    }

    private void testSinglePaymentSoldProductsAssertions$$2() {
        assertSoldProductFive(1, 1);
        assertSoldProductThree(2, 2);
        assertSoldTotalPrice(6240);

    }

    private void testSinglePaymentPaidProductsAssertions$$2() {
        assertPaidProductFive(1,2);
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


    private void testSinglePaymentSoldProductsAssertions$$3() {
        assertSoldProductThree(1, 2);
        assertSoldTotalPrice(5800);
    }

    private void testSinglePaymentPaidProductsAssertions$$3() {
        assertPaidProductFive(1, 3);
        assertPaidTotalPrice(1320);
        assertPreviousPartialPrice(0);
    }

    private void testSinglePaymentPaidProductsAssertionsAfterPay$$2() {
        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(1320);
        assertNoPaidProduct();
    }

    @Test
    public void testSinglePaymentWithDiscountAbsolute() {
        paySingle(2);
        clickOnDiscountAbsolute();
        setDiscountAbsolute("1000");
        pay();
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 1);
        assertNoPaidProduct();
        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(1900);
    }

    @Test
    public void testSinglePaymentWithDiscountPercent() {
        paySingle(2);
        clickOnDiscountPercent();
        setDiscountPercent("30");
        pay();
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 1);
        assertNoPaidProduct();
        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(2030);
    }

    @Test
    public void testPartialPaymentNotAllowed() {
        payPartial(1, 0.5);
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 2);
        assertNoPaidProduct();
        verifyErrorMessageWithParam("PaymentView.ProductNotPartiallyPayable", PRODUCT_FIVE_LONG);
    }

    @Test
    public void testPartialPaymentAmountBigger() {
        payPartial(2, 2.5);
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 2);
        assertNoPaidProduct();
        verifyErrorMessage("PaymentView.PartialPayBiggerAmountError");
    }

    @Test
    public void testPartialPaymentIllegalInput() {
        setTextField(PARTIAL_PAYMENT_VALUE, "NotDouble");
        payPartial(2);
        verifyErrorMessage("PaymentView.PartialPayNumberError");
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 2);
        assertNoPaidProduct();
    }

    @Test
    public void testPartialPaymentOfAProduct() {
        payPartial(2, 0.5);
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 1.5);
        assertSoldTotalPrice(5670);
        assertPaidProductThree(1, 0.5);
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
        assertSoldProductFive(1, 3);
        assertSoldTotalPrice(1320);
        assertPaidProductThree(1, 0.5);
        assertPaidTotalPrice(1450);
        assertPreviousPartialPrice(4350);
        pay();
    }

    private void payPartialOneAndHalf() {
        payPartial(2, 1.5);
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 0.5);
        assertSoldTotalPrice(2770);
        assertPaidProductThree(1, 1.5);
        assertPaidTotalPrice(4350);
        assertPreviousPartialPrice(0);
    }

    @Test
    public void testSinglePaymentPartialValue() {
        payPartialOneAndHalf();
        pay();
        paySingle(2);
        assertSoldProductFive(1, 3);
        assertSoldTotalPrice(1320);
        assertPaidProductThree(1, 0.5);
        assertPaidTotalPrice(1450);
        assertPreviousPartialPrice(4350);
        pay();
    }

    @Test
    public void testFullPaymentWhenSoldProductsEmptyInCaseOfSelectivePayment() {
        paySelective(1);
        paySelective(1);
        clickOnThenWait(SINGLE_PAYMENT, 100);
        pay();
        openTable(TABLE_NUMBER);
        clickOnThenWait(TABLE_NUMBER, 200);
        clickOnThenWait(TO_PAYMENT, 200);
    }

    @Test
    public void testFullPaymentWhenSoldProductsEmptyInCaseOfFullPayment() {
        paySelective(1);
        paySelective(1);
        pay();
        openTable(TABLE_NUMBER);
        clickOnThenWait(TABLE_NUMBER, 200);
        clickOnThenWait(TO_PAYMENT, 200);
    }

    @Test
    public void testPartialPaymentInvalidRange() {
        payPartial(1.01);
        verifyErrorMessage("PaymentView.PartialPayNumberErrorRange");
    }

    @Test
    public void testPartialPaymentInvalidInput() {
        setTextField(PARTIAL_PAYMENT_VALUE, String.valueOf("Invalid"));
        clickButtonThenWait(PARTIAL_PAYMENT, 50);
        pay();
        verifyErrorMessage("PaymentView.PartialPayNumberErrorRange");
        clickButtonThenWait(PARTIAL_PAYMENT, 50);
    }

    @Test
    public void testPartialPaymentOfTheTable() {
        payPartial(0.43);
        assertSoldProductFive(1, 0.57 * 3);
        assertSoldProductThree(2, 0.57 * 2);
        assertSoldTotalPrice((int)Math.round(0.57 * 7120));
        assertPreviousPartialPrice((int)Math.round(0.43 * 7120));
    }

    @Test
    public void testPartialPaymentWithPaidProductsNotEmpty() {
        payPartial(2, 0.5);
        payPartial(0.5);
        assertSoldProductFive(1, 3);
        assertSoldProductThree(2, 1.5);
        assertPreviousPartialPrice(1450);
    }

    @Test
    public void testPartialPaymentOfTheTableWithDiscountAbsolute() {
        setDiscountAbsolute("1000");
        clickOnDiscountAbsolute();
        payPartial(0.43);
        clickOnDiscountAbsolute();
        assertSoldProductFive(1, 0.57 * 3);
        assertSoldProductThree(2, 0.57 * 2);
        assertSoldTotalPrice((int)Math.round(0.57 * 7120));
        assertPreviousPartialPrice((int)Math.round(0.43 * 7120) - 1000);
    }

    @Test
    public void testPartialPaymentOfTheTableWithDiscountPercent() {
        setDiscountPercent("20");
        clickOnDiscountPercent();
        payPartial(0.43);
        clickOnDiscountPercent();
        assertSoldProductFive(1, 0.57 * 3);
        assertSoldProductThree(2, 0.57 * 2);
        assertSoldTotalPrice((int)Math.round(0.57 * 7120));
        int round = (int) Math.round(0.43 * 7120);
        assertPreviousPartialPrice((int)Math.round(round * 0.80));
    }

    @Test
    public void testSelectivePaymentWithoutPaidProduct() {
        clickOnThenWait(SINGLE_PAYMENT, 100);
        pay();
        verifyErrorMessage("PaymentView.SelectivePaymentNoPaidProduct");
        clickOnThenWait(SINGLE_PAYMENT, 100);
    }

    @Test
    public void testMovePaidProductBackToSoldProducts() {
        paySingle(1);
        assertSoldProductFive(1, 2);
        assertSoldTotalPrice(6680);
        assertPaidProductFive(1, 1);
        assertPaidTotalPrice(440);

        paySingle(1);
        assertSoldProductFive(1, 1);
        assertSoldTotalPrice(6240);
        assertPaidProductFive(1, 2);
        assertPaidTotalPrice(880);

        paySingle(1);
        assertSoldProductThree(1, 2);
        assertNumberOfSoldProducts(1);
        assertSoldTotalPrice(5800);
        assertPaidProductFive(1, 3);
        assertPaidTotalPrice(1320);

        putBackToSold(1);
        assertSoldProductFive(1, 1);
        assertSoldTotalPrice(6240);
        assertPaidProductFive(1, 2);
        assertPaidTotalPrice(880);

        putBackToSold(1);
        assertSoldProductFive(1, 2);
        assertSoldTotalPrice(6680);
        assertPaidProductFive(1, 1);
        assertPaidTotalPrice(440);
        putBackToSold(1);
        assertSoldProductFive(1, 3);
        assertSoldTotalPrice(7120);
        assertNoPaidProduct();
        assertPaidTotalPrice(0);

        paySelective(1);
        assertSoldProductThree(1, 2);
        assertNumberOfSoldProducts(1);
        assertSoldTotalPrice(5800);
        assertPaidProductFive(1, 3);
        assertPaidTotalPrice(1320);

        pay();
        assertNoPaidProduct();
        assertPaidTotalPrice(0);
        assertPreviousPartialPrice(1320);
    }

    @Test
    public void testMovePaidProductBackToSoldProductsForPartialPayment() {
        payPartial(2, 0.5);
        assertPaidProductThree(1, 0.5);
        assertSoldProductThree(2, 1.5);
        putBackToSold(1);
        assertSoldProductThree(2, 2);
    }

    @Test
    public void testManualGameFee() {
        sellGameFee();
        assertSoldProduct(3, GAME_FEE, 1, 300, 300);
        assertSoldTotalPrice(7420);
        assertNumberOfSoldProducts(3);

        sellGameFee();
        assertSoldProduct(3, GAME_FEE, 2, 300, 600);
        assertSoldTotalPrice(7720);
        assertNumberOfSoldProducts(3);
    }

    @Test
    public void testAutoGameFee() {
        guestPlus();    // 1
        autoGameFee();
        assertNumberOfSoldProducts(2);
        guestPlus();    // 2
        autoGameFee();
        assertNumberOfSoldProducts(2);
        guestPlus();    // 3
        autoGameFee();
        assertNumberOfSoldProducts(2);
        guestPlus();    // 4
        autoGameFee();
        assertNumberOfSoldProducts(3);
        assertSoldProduct(3, GAME_FEE, 1, 300, 300);
        sleep(2000);
        guestPlus();    //5
        autoGameFee();
        assertNumberOfSoldProducts(3);
        assertSoldProduct(3, GAME_FEE, 3, 300, 900);

        clickButtonThenWait(TO_SALE, 500);
        selectiveCancellation(GAME_FEE);
        sellAdHocProduct("Test", 1, 500, 879);
        clickOnThenWait(TO_PAYMENT, 200);

        assertSoldTotalPrice(7999);
        guestMinus();
        autoGameFee();
        assertNumberOfSoldProducts(4);
        assertSoldProduct(4, GAME_FEE, 1, 300, 300);

        clickButtonThenWait(TO_SALE, 500);
        selectiveCancellation(GAME_FEE);
        selectiveCancellation("Test");
        sellAdHocProduct("Test", 1, 500, 880);
        clickOnThenWait(TO_PAYMENT, 200);

        assertSoldTotalPrice(8000);
        autoGameFee();
        assertNumberOfSoldProducts(3);
    }

    @Test
    public void testDiscountAbsoluteInvalidInput() {
        clickOnDiscountAbsolute();
        setDiscountAbsolute("Invalid");
        pay();
        verifyErrorMessage("PaymentView.DiscountAbsoluteNumberFormatError");
        clickOnDiscountAbsolute();
    }

    @Test
    public void testDiscountPercentInvalidInput() {
        clickOnDiscountPercent();
        setDiscountPercent("Invalid");
        pay();
        verifyErrorMessage("PaymentView.DiscountPercentNumberFormatError");
        sleep(5000);

        setDiscountPercent("120");
        pay();
        verifyErrorMessage("PaymentView.DiscountPercentNumberFormatError");
        sleep(5000);

        setDiscountPercent("-5");
        pay();
        verifyErrorMessage("PaymentView.DiscountPercentNumberFormatError");
        clickOnDiscountPercent();
    }

    @Test
    public void testBackToSaleView() {
        clickButtonThenWait(TO_SALE, 500);
        clickOnThenWait(TO_PAYMENT, 200);
    }

    @Test
    public void testBackToRestaurantView() {
        backToRestaurantView();
        enterSaleView(TABLE_NUMBER);
        enterPaymentView();
    }

    @Test
    public void testClearInputFieldsWhenEnterThePaymentView() {
        setDiscountPercent("50");
        setDiscountAbsolute("5000");
        setPartialPaymentValue(1.5);
        backToRestaurantView();
        enterSaleView(TABLE_NUMBER);
        enterPaymentView();
        assertEquals("", getTextField(DISCOUNT_PERCENT_VALUE));
        assertEquals("", getTextField(DISCOUNT_ABSOLUTE_VALUE));
        assertEquals("", getTextField(PARTIAL_PAYMENT_VALUE));
    }

    @After
    public void payTable() {
        pay();
    }
}
