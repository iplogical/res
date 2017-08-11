package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.junit.Test;

import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.TABLE_TEST_TABLE;
import static com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema.TABLE_TEST_TABLE_NAME;
import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TableControllerTest extends TestFXBase {

    @Test
    public void testMeepleVisible() {
        addTableToTab("TestTableOne", TABLE_TEST_TABLE, "Restaurant.Frequenters");
        assertTrue(findMeepleOfTable(TABLE_TEST_TABLE_NAME).isVisible());
        assertEquals("1", findHostedCountOfTable(TABLE_TEST_TABLE_NAME).getText());

        addTableToTab("TestTableTwo", TABLE_TEST_TABLE, "Restaurant.Loiterers");
        assertTrue(findMeepleOfTable(TABLE_TEST_TABLE_NAME).isVisible());
        assertEquals("2", findHostedCountOfTable(TABLE_TEST_TABLE_NAME).getText());

        deleteTableFromTab("TestTableOne", "Restaurant.Frequenters");
        assertTrue(findMeepleOfTable(TABLE_TEST_TABLE_NAME).isVisible());
        assertEquals("1", findHostedCountOfTable(TABLE_TEST_TABLE_NAME).getText());

        deleteTableFromTab("TestTableTwo", "Restaurant.Loiterers");
        assertFalse(findMeepleOfTable(TABLE_TEST_TABLE_NAME).isVisible());
        assertEquals("", findHostedCountOfTable(TABLE_TEST_TABLE_NAME).getText());
    }

    private ImageView findMeepleOfTable(String table) {
        StackPane stackPane = findStackPaneOfTable(table);
        return  (ImageView)stackPane.lookup("#meeple");
    }

    private Label findHostedCountOfTable(String table) {
        StackPane stackPane = findStackPaneOfTable(table);
        return (Label)stackPane.lookup("#hostedCount");
    }

    private StackPane findStackPaneOfTable(String table) {
        Label tableName = find(table);
        return (StackPane)tableName.getParent().getParent();
    }
}
