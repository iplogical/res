package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.model.utils.Functor;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import org.testfx.api.FxRobotException;

import static com.inspirationlogical.receipt.waiter.utility.CSSUtilities.BACKGROUND_OPEN;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.CONFIGURATION_ID;
import static java.lang.Thread.sleep;
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
}
