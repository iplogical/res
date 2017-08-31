package com.inspirationlogical.receipt.manager.utility;

import com.inspirationlogical.receipt.manager.viewmodel.RecipeViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.CLOSE;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.RECIPE_FORM_COMPONENTS_TABLE;
import static org.junit.Assert.assertNotEquals;

public class RecipeFormUtils extends AbstractUtils {

    public static void clickOnClose() {
        clickButtonThenWait(CLOSE, 200);
    }

    private static ObservableList<RecipeViewModel> getComponents() {
        TableView<RecipeViewModel> components = robot.find(RECIPE_FORM_COMPONENTS_TABLE);
        return components.getItems();
    }

    public static void assertComponent(String name) {
        assertNotEquals(0, getComponents().stream().filter(recipeViewModel -> recipeViewModel.getComponent().equals(name)).count());
    }
}
