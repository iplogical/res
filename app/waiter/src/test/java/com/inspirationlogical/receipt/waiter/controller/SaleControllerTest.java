package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import javafx.scene.control.Label;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by TheDagi on 2017. 05. 09..
 */
public class SaleControllerTest extends TestFXBase {

    @Override
    @Before
    public void setUpClass() throws Exception {
        ApplicationTest.launch(WaiterApp.class);
        clickOn("TestTable8");
    }

    @Test
    public void testClickOnGuestPlus() {
        clickOn(GUEST_PLUS);
        sleep(2000);
        assertEquals("8 (1/4)", ((Label)find(TABLE_NUMBER)).getText());
    }
}
