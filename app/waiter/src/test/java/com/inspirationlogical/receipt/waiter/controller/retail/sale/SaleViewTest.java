package com.inspirationlogical.receipt.waiter.controller.retail.sale;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Before;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickOnThenWait;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.TO_RESTAURANT;

public class SaleViewTest extends TestFXBase {

    protected static final String TABLE_NAME = "TestTableName20";
    protected static final String TABLE_NUMBER = "20";
    protected static final String TABLE_GUESTS = "0";
    protected static final String TABLE_CAPACITY = "4";

    /*
    *   If these tests run on the database created by the SchemaBuilderApp the commented lines in the
    *   functions below make them run independently of all other UI tests.
    *   However the required time is increased significantly.
    */

    @Before
    public void setUpSaleControllerTest() throws Exception {
//        addTable(TABLE_NAME, TABLE_NUMBER, TABLE_GUESTS, TABLE_CAPACITY);
//        openTable(TABLE_NUMBER);
        clickOnThenWait(TABLE_NUMBER, 200);
    }

    @After
    public void backToRestaurantView() {
        clickButtonThenWait(TO_RESTAURANT, 500);
//        clickOnThenWait(Resources.WAITER.getString("SaleView.ToPaymentView"), 500);
//        clickOnThenWait(Resources.WAITER.getString("PaymentView.Pay"), 500);
//        runInConfigurationMode(() -> deleteTable(TABLE_NUMBER));
    }
}
