package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.*;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.verifyThatVisible;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.TABLEFORM_CONFIRM;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.*;

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
        setTextField(TABLEFORM_NUMBER, "");
        clickOn(TABLEFORM_CONFIRM);
        verifyErrorMessage("TableForm.NumberFormatError");
        clickOn(TABLEFORM_CANCEL);
    }

    @Test
    public void testMergeAndSplitTables() {
        mergeTables(CONSUMER_TEST_TABLE, CONSUMED_TEST_TABLE_ONE);
        splitTables(CONSUMER_TEST_TABLE);
        closeTable(CONSUMER_TEST_TABLE);
    }

    @Test
    public void testVirtualTableNotAllowedForMerge() {
        addTableToTab("TestTable", "100", "Restaurant.Frequenters");
        selectTab("Restaurant.Frequenters");
        runInConfigurationMode(() -> {
            clickOnThenWait("TestTable", 100);
            selectTab("Restaurant.Tables");
            clickOnThenWait(CONSUMED_TEST_TABLE_ONE, 100);
            longClickOn(new Point2D(150, 150));
            clickMenuThenWait("ContextMenu.MergeTable", 100);
            verifyErrorMessage("InsufficientSelection");
        });
        deleteTableFromTab("TestTable", "Restaurant.Frequenters");
    }
}
