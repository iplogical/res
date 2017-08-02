package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.model.utils.Functor;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.testfx.api.FxRobotInterface;

import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.TABLEFORM_CAPACITY;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.TABLEFORM_CONFIRM;
import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;

public class ClickUtils {

    static private TestFXBase robot;

    public static void setRobot(TestFXBase robot) {
        ClickUtils.robot = robot;
    }

    public static void runInConfigurationMode(Functor f) {
        robot.clickOn(CONFIGURATION_ID);
        f.doIt();
        robot.clickOn(CONFIGURATION_ID);
    }

    public static void longClickOn(String query) {
        robot.drag(query, MouseButton.PRIMARY);
        robot.sleep(300);
        robot.drop();
    }

    public static void longClickOn(Point2D position) {
        robot.drag(position.getX(), position.getY());
        robot.sleep(300);
        robot.drop();
    }

    public static void clickOnThenWait(String string, int milliseconds) {
        robot.clickOn(string);
        robot.sleep(milliseconds);
    }

    public static void clickOnThenWait(Point2D point, int milliseconds) {
        robot.clickOn(point);
        robot.sleep(milliseconds);
    }

    public static void clickButtonThenWait(String buttonName, int milliSec) {
        clickOnThenWait(Resources.WAITER.getString(buttonName), milliSec);
    }

    public static void clickMenuThenWait(String buttonName, int milliSec) {
        clickOnThenWait(Resources.WAITER.getString(buttonName), milliSec);
    }

    public static void setTextField(String fxId, String text) {
        ((TextField)robot.find(fxId)).setText(text);
    }

    public static String getTextField(String fxId) {
        return ((TextField)robot.find(fxId)).getText();
    }

    public static String getLabel(String fxId) {
        return ((Label)robot.find(fxId)).getText();
    }

    public static void setTextArea(String fxId, String text) {
        ((TextArea)robot.find(fxId)).setText(text);
    }

    public static void verifyThatVisible(String nodeQuery) {
        verifyThat(nodeQuery, Node::isVisible);
    }

    public static void verifyThatNotVisible(String nodeQuery) {
        try {
            verifyThat(nodeQuery, Node::isVisible);
            fail("Should not find the node");
        } catch (NullPointerException e) {}
    }

    public static void openTable(String tableNumber) {
        longClickOn(tableNumber);
        clickOnThenWait(Resources.WAITER.getString("ContextMenu.OpenTable"), 500);
    }

    public static void addTable(String tableName, String number, String guestCount, String capacity) {
        runInConfigurationMode(() -> {
            longClickOn(new Point2D(150, 150));
            clickOnThenWait(Resources.WAITER.getString("ContextMenu.AddTable"), 100);
            ((TextField)robot.find(TABLEFORM_NAME)).setText(tableName);
            ((TextField)robot.find(TABLEFORM_NUMBER)).setText(number);
            ((TextField)robot.find(TABLEFORM_GUEST_COUNT)).setText(guestCount);
            ((TextField)robot.find(TABLEFORM_CAPACITY)).setText(capacity);
            clickOnThenWait(TABLEFORM_CONFIRM, 100);
            verifyThatVisible(tableName);
            verifyThatVisible(number);
            verifyThatVisible(guestCount);
            verifyThatVisible(capacity);
        });
    }

    public static void deleteTable(String tableNumber) {
        runInConfigurationMode(() -> {
            longClickOn(tableNumber);
            robot.clickOn(Resources.WAITER.getString("ContextMenu.DeleteTable"));
            verifyThatNotVisible(tableNumber);
        });
    }

    public static FxRobotInterface type(String text) {
        FxRobotInterface robotInterface = robot;
        for(Character c : text.toCharArray()) {
            KeyCode current = KeyCode.getKeyCode(c.toString().toUpperCase());
            if(c == ' ') {
                current = KeyCode.SPACE;
            } else if(c == ',') {
                current = KeyCode.COMMA;
            }
            robotInterface = robotInterface.type(current);
        }
        return robotInterface;
    }

}