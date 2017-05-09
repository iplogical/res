package com.inspirationlogical.receipt.waiter.controller;

import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Created by TheDagi on 2017. 05. 09..
 */
public class RestaurantControllerTest extends TestFXBase {

    @Test
    public void clickOnMotion() {
        clickOn(MOTION_ID);
        sleep(1000);
        clickOn(CONFIGURATION_ID);
        sleep(1000);
        verifyThat(MOTION_ID, node -> {return true;});
    }

    @Test
    public void clickOnTable() {
        clickOn("TestTable8");
        sleep(5000);
        verifyThat(MOTION_ID, node -> {return true;});
    }
}
