package com.inspirationlogical.receipt.waiter.controller.retail.sale;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Before;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.SALE_TEST_TABLE;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickOnThenWait;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.TO_RESTAURANT;

public class SaleViewTest extends TestFXBase {

    protected static final String TABLE_NAME = "TestTableName20";
    protected static final String TABLE_NUMBER = SALE_TEST_TABLE;
    protected static final String TABLE_GUESTS = "0";
    protected static final String TABLE_CAPACITY = "6";

    @Before
    public void setUpSaleControllerTest() throws Exception {
        clickOnThenWait(TABLE_NUMBER, 200);
    }

    @After
    public void backToRestaurantView() {
        clickButtonThenWait(TO_RESTAURANT, 500);
    }
}
