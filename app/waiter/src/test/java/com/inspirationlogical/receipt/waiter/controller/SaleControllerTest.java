package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.utility.ClickUtils;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickOnThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.openTable;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.verifyThatVisible;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by TheDagi on 2017. 05. 09..
 */
public class SaleControllerTest extends TestFXBase {

    @Before
    public void setUpSaleControllerTest() throws Exception {
        super.setUpClass();
//        openTable("7");
        clickOnThenWait("7", 500);
    }

    @Test
    public void testClickOnGuestPlus() {
        clickOnThenWait(GUEST_PLUS, 200);
        assertEquals("7 (1/2)", ((Label)find(TABLE_NUMBER)).getText());
        clickOnThenWait(GUEST_MINUS, 200);
        assertEquals("7 (0/2)", ((Label)find(TABLE_NUMBER)).getText());
        sleep(2000);
    }

    @Test
    public void testSelectCategory() {
        clickOnThenWait("Sörök", 100);
        verifyThatVisible("Sop 0,5L");
        sleep(2000);
    }

    @Test
    public void testSellProduct() {
        clickOnThenWait("Sörök", 100);
        clickOnThenWait("Sop 0,5L", 100);
        TableView<SoldProductViewModel> tableView = find(SOLD_PRODUCTS_TABLE);
        ObservableList<SoldProductViewModel> items = tableView.getItems();
        assertEquals("Soproni 0,5L", items.get(0).getProductName());
        assertEquals("1.0", items.get(0).getProductQuantity());
        assertEquals("440", items.get(0).getProductUnitPrice());
        assertEquals("440", items.get(0).getProductTotalPrice());
//        verifyThat(tableView, tableView1 -> tableView1.getSelectionModel().);
        clickOnThenWait(Resources.WAITER.getString("SaleView.SelectiveCancellation"), 100);
        clickOnThenWait("Soproni 0,5L", 100);
    }

    @After
    public void backToRestaurantView() {
        clickOnThenWait(Resources.WAITER.getString("Common.BackToRestaurantView"), 500);
    }
}
