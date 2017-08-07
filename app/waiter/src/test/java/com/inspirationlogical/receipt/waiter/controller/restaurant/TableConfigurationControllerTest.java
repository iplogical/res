package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.geometry.Point2D;
import org.junit.Test;

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
        addTable("NumberUsed", "1", "26", "36");
        verifyErrorMessageWithParam("TableAlreadyUsed", "1");
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
        openThenCloseTable("1");
    }

    @Test
    public void testEditTable() {
        longClickOn("1");
        clickMenuThenWait("ContextMenu.EditTable", 500);
        setTextField(TABLEFORM_NAME, "MyNewName");
        setTextField(TABLEFORM_GUEST_COUNT, "26");
        setTextField(TABLEFORM_CAPACITY, "36");
        setTextArea(TABLEFORM_NOTE, "Hello Table");
        clickOn(TABLEFORM_CONFIRM);
        verifyThatVisible("MyNewName");
        verifyThatVisible("26");
        verifyThatVisible("36");
        openThenCloseTable("1");
        verifyThatNotVisible("MyNewName");
    }

    @Test
    public void testEditTableNumberAlreadyUsed() {
        longClickOn("1");
        clickMenuThenWait("ContextMenu.EditTable", 500);
        setTextField(TABLEFORM_NUMBER, "3");
        clickOn(TABLEFORM_CONFIRM);
        verifyErrorMessageWithParam("TableAlreadyUsed", "3");
    }

    @Test
    public void testMergeAndSplitTables() {
        mergeTables("1", "3");
        splitTables("1");
        closeTable("1");
    }

}
