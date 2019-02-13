package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import org.junit.After;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.*;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.NameUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.SaleUtils.*;

public class TableConfigurationControllerTest extends TestFXBase {

    @Test
    public void testAddNewTableThenDeleteIt() {
        addTable("TestTableOne", "100", "26", "36");
        openThenCloseTable("100");
        deleteTable("100");
    }

    @Test
    public void testAddNewTableNumberAlreadyUsed() {
        addTable("NumberUsed", TABLE_TEST_TABLE, "26", "36");
        verifyErrorMessageWithParam("TableAlreadyUsed", TABLE_TEST_TABLE);
        clickOn(TABLEFORM_CANCEL);
    }

    @Test
    public void testAddFrequenterTable() {
        selectTab("Restaurant.Frequenters");
        addTable("TestTableOne", "100", "26", "36");
        deleteTable("TestTableOne");
        verifyThatNotVisible("TestTableOne");
        selectTab("Restaurant.Tables");
    }

    @Test
    public void testAddLoitererTable() {
        selectTab("Restaurant.Loiterers");
        addTable("TestTableOne", "100", "26", "36");
        deleteTable("TestTableOne");
        verifyThatNotVisible("TestTableOne");
        selectTab("Restaurant.Tables");
    }

    @Test
    public void testAddEmployeesTable() {
        selectTab("Restaurant.Employees");
        addTable("TestTableOne", "100", "26", "36");
        deleteTable("TestTableOne");
        verifyThatNotVisible("TestTableOne");
        selectTab("Restaurant.Tables");
    }

    @Test
    public void testVirtualTableKeepsItsNameWhenClosed() {
        selectTab("Restaurant.Employees");
        addTable("TestTableOne", "100", "26", "36");
        openThenCloseTable("TestTableOne");
        verifyThatVisible("TestTableOne");
        deleteTable("TestTableOne");
        verifyThatNotVisible("TestTableOne");
        selectTab("Restaurant.Tables");
    }

    private void openThenCloseTable(String tableNumber) {
        openTable(tableNumber);
        closeTable(tableNumber);
    }

    @Test
    public void testOpenThenCloseTable() {
        openThenCloseTable(TABLE_TEST_TABLE);
    }

    @Test
    public void testEditTable() {
        longClickOn(TABLE_TEST_TABLE);
        clickMenuThenWait("ContextMenu.EditTable", 500);
        setTextField(TABLEFORM_NAME, "MyNewName");
        setTextField(TABLEFORM_GUEST_COUNT, "26");
        setTextField(TABLEFORM_CAPACITY, "36");
        setTextArea(TABLEFORM_NOTE, "Hello Table");
        clickOn(TABLEFORM_CONFIRM);
        verifyThatVisible("MyNewName");
        verifyThatVisible("26");
        verifyThatVisible("36");
        openThenCloseTable(TABLE_TEST_TABLE);
        verifyThatNotVisible("MyNewName");
    }

    @Test
    public void testEditTableNumberAlreadyUsed() {
        longClickOn(TABLE_TEST_TABLE);
        clickMenuThenWait("ContextMenu.EditTable", 500);
        setTextField(TABLEFORM_NUMBER, SALE_TEST_TABLE);
        clickOn(TABLEFORM_CONFIRM);
        verifyErrorMessageWithParam("TableAlreadyUsed", SALE_TEST_TABLE);
    }

    @Test
    public void testEditTableInvalidInput() {
        longClickOn(TABLE_TEST_TABLE);
        clickMenuThenWait("ContextMenu.EditTable", 500);
        setTextField(TABLEFORM_NUMBER, "Invalid");
        clickOn(TABLEFORM_CONFIRM);
        verifyErrorMessage("TableForm.NumberFormatError");
        clickOn(TABLEFORM_CANCEL);
    }


    @Test
    public void testExchangeTableInsufficientSelection() {
        runInConfigurationMode(() -> {
            clickOnThenWait(TABLE_TEST_TABLE, 100);
            longClickOn(TABLE_TEST_TABLE);
            clickMenuThenWait("ContextMenu.ExchangeTable", 100);
            verifyErrorMessage("TableConfiguration.InsufficientForExchange");
        });
    }

    @Test
    public void testExchangeTableTooManySelection() {
        runInConfigurationMode(() -> {
            clickOnThenWait(TABLE_TEST_TABLE, 100);
            clickOnThenWait(RESTAURANT_TEST_TABLE, 100);
            clickOnThenWait(RESERVATION_TEST_TABLE, 100);
            longClickOn(TABLE_TEST_TABLE);
            clickMenuThenWait("ContextMenu.ExchangeTable", 100);
            verifyErrorMessage("TableConfiguration.InsufficientForExchange");
        });
    }

    @Test
    public void testExchangeTableOneIsOpen() {
        openTableAndSellProducts(TABLE_TEST_TABLE, PRODUCT_FIVE);
        exchangeTables(TABLE_TEST_TABLE, RESTAURANT_TEST_TABLE);
        enterSaleView(RESTAURANT_TEST_TABLE);
        assertSoldProductFive(1, 6);
        backToRestaurantView();
        closeTable(RESTAURANT_TEST_TABLE);
    }

    @Test
    public void testExchangeTableBothAreOpen() {
        openTableAndSellProducts(TABLE_TEST_TABLE, PRODUCT_FIVE);
        openTableAndSellProducts(RESTAURANT_TEST_TABLE, PRODUCT_TWO);
        exchangeTables(TABLE_TEST_TABLE, RESTAURANT_TEST_TABLE);

        enterSaleView(RESTAURANT_TEST_TABLE);
        assertSoldProductFive(1, 6);
        backToRestaurantView();
        closeTable(RESTAURANT_TEST_TABLE);

        enterSaleView(TABLE_TEST_TABLE);
        assertSoldProduct(1, PRODUCT_TWO_LONG + " *", 6, 133, 798);
        backToRestaurantView();
        closeTable(TABLE_TEST_TABLE);
    }

    private void exchangeTables(String firstTable, String secondTable) {
        runInConfigurationMode(() -> {
            clickOnThenWait(firstTable, 100);
            clickOnThenWait(secondTable, 100);
            longClickOn(secondTable);
            clickMenuThenWait("ContextMenu.ExchangeTable", 100);
        });
    }

    private void openTableAndSellProducts(String tableName, String product) {
        openTable(tableName);
        enterSaleView(tableName);
        selectCategory(AGGREGATE_ONE);
        sellProduct(product, 6);
        backToRestaurantView();
    }

    @After
    public void waitOneSecAfterTest() throws Exception {
        Thread.sleep(1000);
    }
}
