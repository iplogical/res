package com.inspirationlogical.receipt.manager.utility;

import com.inspirationlogical.receipt.manager.viewmodel.RecipeViewModel;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.TableView;

import java.util.List;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.selectChoiceBoxItem;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.setTextField;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RecipeFormUtils extends AbstractUtils {

    private static final int ROW_HEIGHT = 30;
    private static final int QUANTITY_X = 720;
    private static final int QUANTITY_Y = 235;

    public static void clickOnClose() {
        clickButtonThenWait(CLOSE, 200);
    }

    private static ObservableList<RecipeViewModel> getComponents() {
        TableView<RecipeViewModel> components = robot.find(RECIPE_FORM_COMPONENTS_TABLE);
        return components.getItems();
    }

    public static void assertComponent(String name) {
        List<RecipeViewModel> recipeList = getComponent(name);
        assertNotEquals(0, recipeList.size());
    }

    private static List<RecipeViewModel> getComponent(String name) {
        return getComponents().stream()
                .filter(recipeViewModel -> recipeViewModel.getComponentLongName().equals(name))
                .collect(toList());
    }

    public static void assertNoComponent(String name) {
        List<RecipeViewModel> recipeList = getComponent(name);
        assertEquals(0, recipeList.size());
    }

    public static void assertComponent(String name, double quantity) {
        List<RecipeViewModel> recipeList = getComponent(name);
        assertNotEquals(0, recipeList.size());
        assertEquals(recipeList.get(0).getQuantity(), String.valueOf(quantity));
    }

    public static void selectProduct(int n) {
        selectChoiceBoxItem(RECIPE_FORM_PRODUCTS, n);
    }

    public static void selectComponent(int n) {
        selectChoiceBoxItem(RECIPE_FORM_COMPONENTS, n);
    }

    public static void setQuantity(Double quantity) {
        setTextField(RECIPE_FORM_QUANTITY, quantity.toString());
    }

    public static void editQuantity(int row, double quantity) {
        robot.doubleClickOn(new Point2D(QUANTITY_X, QUANTITY_Y + row * ROW_HEIGHT));
        robot.write(String.valueOf(quantity));
    }

    public static void clickOnComponent(int row) {
        robot.clickOn(new Point2D(QUANTITY_X, QUANTITY_Y + row * ROW_HEIGHT));
    }

    public static void addComponent() {
        clickButtonThenWait(RECIPE_FORM_ADD_COMPONENT, 100);
    }

    public static void deleteComponent() {
        clickButtonThenWait(RECIPE_FORM_REMOVE_COMPONENT, 100);
    }
}
