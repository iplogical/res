package com.inspirationlogical.receipt.controller;

import static com.inspirationlogical.receipt.controller.AddTableFormControllerImpl.ADD_TABLE_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.controller.ContextMenuControllerImpl.CONTEXT_MENU_VIEW_PATH;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.inspirationlogical.receipt.service.RestaurantServices;
import com.inspirationlogical.receipt.view.NodeUtility;
import com.inspirationlogical.receipt.view.PressAndHoldHandler;
import com.inspirationlogical.receipt.view.ViewLoader;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

@PrepareForTest({PressAndHoldHandler.class, ViewLoader.class, NodeUtility.class})
@RunWith(PowerMockRunner.class)
public class RestaurantControllerTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private AnchorPane layout;

    @Mock
    private VBox contextMenu;

    @Mock
    private VBox addTableForm;

    @Mock
    private ContextMenuController contextMenuController;

    @Mock
    private AddTableFormController addTableFormController;

    @Mock
    private RestaurantServices restaurantServices;

    private JFXPanel jfxPanel;

    private RestaurantControllerImpl underTest;

    @Before
    public void setUp() {
        mockStatic(ViewLoader.class);
        when(ViewLoader.loadViewHidden(CONTEXT_MENU_VIEW_PATH, contextMenuController)).thenReturn(contextMenu);
        when(ViewLoader.loadViewHidden(ADD_TABLE_FORM_VIEW_PATH, addTableFormController)).thenReturn(addTableForm);

        jfxPanel = new JFXPanel();
        underTest = new RestaurantControllerImpl(restaurantServices, contextMenuController, addTableFormController);
        underTest.layout = layout;
    }

    @Test
    public void shouldInitializeContextMenu() {
        // Given
        mockStatic(PressAndHoldHandler.class);

        // When
        underTest.initialize(null, null);

        // Then
        verify(layout.getChildren()).add(contextMenu);

        verifyStatic(times(1));
        ViewLoader.loadViewHidden(eq(CONTEXT_MENU_VIEW_PATH), eq(contextMenuController));

        verifyStatic(times(1));
        PressAndHoldHandler.addPressAndHold(eq(layout), any(), any());
    }

    @Test
    public void shouldInitializeAddTableForm() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then
        verify(layout.getChildren()).add(addTableForm);

        verifyStatic(times(1));
        ViewLoader.loadViewHidden(eq(ADD_TABLE_FORM_VIEW_PATH), eq(addTableFormController));
    }

    @Test
    public void shouldInitializeRestaurant() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then
        verify(restaurantServices).getActiveRestaurant();
    }

    @Test
    public void shouldInitializeTables() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then
        verify(restaurantServices).getTables(any());
    }

    @Test
    public void shouldShowAddTableForm() {
        // Given
        Point2D position = new Point2D(0, 0);

        mockStatic(NodeUtility.class);
        when(NodeUtility.getNodePosition(contextMenu)).thenReturn(position);

        // When
        underTest.initialize(null, null);
        underTest.showAddTableForm();

        // Then
        verifyStatic(times(1));
        NodeUtility.showNode(eq(addTableForm), eq(position));
    }
}
