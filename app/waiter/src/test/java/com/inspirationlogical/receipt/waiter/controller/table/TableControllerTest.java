package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.junit.Test;

import static com.inspirationlogical.receipt.waiter.utility.RestaurantUtils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TableControllerTest extends TestFXBase {

    @Test
    public void testMeepleVisible() {
        addTableToTab("TestTableOne", "Restaurant.Frequenters");
        assertTrue(findMeepleOfTable("Table11").isVisible());
        assertEquals("1", findHostedCountOfTable("Table11").getText());

        addTableToTab("TestTableTwo", "Restaurant.Loiterers");
        assertTrue(findMeepleOfTable("Table11").isVisible());
        assertEquals("2", findHostedCountOfTable("Table11").getText());

        deleteTableFromTab("TestTableOne", "Restaurant.Frequenters");
        assertTrue(findMeepleOfTable("Table11").isVisible());
        assertEquals("1", findHostedCountOfTable("Table11").getText());

        deleteTableFromTab("TestTableTwo", "Restaurant.Loiterers");
        assertFalse(findMeepleOfTable("Table11").isVisible());
        assertEquals("", findHostedCountOfTable("Table11").getText());
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
