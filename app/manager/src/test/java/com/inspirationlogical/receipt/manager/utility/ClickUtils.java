package com.inspirationlogical.receipt.manager.utility;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.testfx.api.FxRobotInterface;
import org.testfx.service.query.EmptyNodeQueryException;

import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.CANCEL;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.CONFIRM;
import static org.junit.Assert.fail;
import static org.testfx.api.FxAssert.verifyThat;

public class ClickUtils extends AbstractUtils {

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
        clickOnThenWait(ManagerResources.MANAGER.getString(buttonName), milliSec);
    }

    public static void clickOnConfirm() {
        clickButtonThenWait(CONFIRM, 500);
    }

    public static void clickOnCancel() {
        clickButtonThenWait(CANCEL, 500);
    }

    public static void setTextField(String fxId, String text) {
        ((TextField)robot.find(fxId)).setText(text);
    }

    public static String getTextField(String fxId) {
        return ((TextField)robot.find(fxId)).getText();
    }

    public static void setTextArea(String fxId, String text) {
        ((TextArea)robot.find(fxId)).setText(text);
    }

    public static String getTextArea(String fxId) {
        return ((TextArea)robot.find(fxId)).getText();
    }

    public static String getLabel(String fxId) {
        return ((Label)robot.find(fxId)).getText();
    }

    public static void setDatePicker(String fxId, String text) {
        ((DatePicker)robot.find(fxId)).getEditor().setText(text);
    }

    public static String getDatePicker(String fxId) {
        return ((DatePicker)robot.find(fxId)).getEditor().getText();
    }

    public static void selectChoiceBoxItem(String fxId, int number) {
        clickOnThenWait(fxId, 100);
        for (int i = 0; i < number; i++) {
            robot.type(KeyCode.DOWN);
        }
        robot.type(KeyCode.ENTER);
    }

    public static void verifyThatVisible(String nodeQuery) {
        verifyThat(nodeQuery, Node::isVisible);
    }

    public static void verifyThatNotVisible(String nodeQuery) {
        try {
            verifyThat(nodeQuery, Node::isVisible);
            fail("Should not find the node");
        } catch (EmptyNodeQueryException e) {}
    }

    public static void verifyErrorMessage(String error) {
        verifyThatVisible(ManagerResources.MANAGER.getString(error));
    }

    public static void verifyErrorMessageWithParam(String error, String param) {
        verifyThatVisible(ManagerResources.MANAGER.getString(error) + param);
    }

    public static FxRobotInterface type(String text) {
        FxRobotInterface robotInterface = robot;
        for(Character c : text.toCharArray()) {
            KeyCode current = KeyCode.getKeyCode(c.toString().toUpperCase());
            if(c == ' ') {
                current = KeyCode.SPACE;
            } else if(c == ',') {
                current = KeyCode.COMMA;
            } else if(c == '.') {
                current = KeyCode.PERIOD;
            }
            robotInterface = robotInterface.type(current);
        }
        return robotInterface;
    }

}
