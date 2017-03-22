package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.controller.TableFormControllerImpl.TABLE_FORM_VIEW_PATH;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.waiter.builder.RestaurantContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.view.NodeUtility;
import com.inspirationlogical.receipt.waiter.view.PressAndHoldHandler;
import com.inspirationlogical.receipt.waiter.view.ViewLoader;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;

import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;

@PrepareForTest({PressAndHoldHandler.class, ViewLoader.class, NodeUtility.class})
@RunWith(PowerMockRunner.class)
public class RestaurantControllerTest {

    private ToggleButton configuration;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AnchorPane tablesTab;

    private Label tablesLab;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private AnchorPane virtualTab;

    private Label virtualLab;

    private Popup tableForm;

    @Mock
    private Pane tableFormContent;

    @Mock
    private TableFormController tableFormController;

    @Mock
    private ConfigureTableFormController configureTableFormController;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestaurantServices restaurantServices;

    @Mock
    private RestaurantView restaurantView;

    @Mock
    RestaurantViewState restaurantViewState;

    @Mock
    private TableView tableView;

    private RestaurantControllerImpl underTest;

    @BeforeClass
    public static void initJFX() throws Exception {
        new JFXPanel();
    }

    @Before
    public void setUp() {
        when(restaurantServices.getActiveRestaurant()).thenReturn(restaurantView);

        mockStatic(PressAndHoldHandler.class);
        mockStatic(ViewLoader.class);
        when(ViewLoader.loadView(TABLE_FORM_VIEW_PATH, tableFormController)).thenReturn(tableFormContent);

        underTest = new RestaurantControllerImpl(restaurantServices, tableFormController, configureTableFormController);
        underTest.tablesTab = tablesTab;
        underTest.tablesLab = tablesLab;
        underTest.virtualTab = virtualTab;
        underTest.virtualLab = virtualLab;
    }

    @Test
    public void shouldInitializeTableForm() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then
        verifyStatic(times(1));
        ViewLoader.loadView(eq(TABLE_FORM_VIEW_PATH), eq(tableFormController));

        //assertTrue(tableForm.getContent().contains(tableFormContent));
    }

    @Test
    public void shouldInitializeContextMenus() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then

        verifyStatic(times(2));
        PressAndHoldHandler.addPressAndHold(any(), any(), any(RestaurantContextMenuBuilderDecorator.class), any());
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
        verify(restaurantServices).getTables(restaurantView);
    }

    @Test
    public void shouldShowAddTableForm() {
        // Given
        Point2D position = new Point2D(0, 0);

        // When
        underTest.initialize(null, null);
        //underTest.showAddTableForm(position);

        // Then
        //assertTrue(tableForm.isShowing());
    }

    @Test
    public void shouldClearTableWhenShowingAddTableForm() {
        // Given
        Point2D position = new Point2D(0, 0);

        // When
        underTest.initialize(null, null);
        //underTest.showAddTableForm(position);

        // Then
        //verify(tableFormController).loadTable(eq(null));
    }

    @Test
    public void shouldCreateTable() {
        // Given
        int tableNumber = 5;
        int tableCapacity = 4;
        boolean isVirtual = false;

        // When
        underTest.initialize(null, null);
        //underTest.createTable(tableNumber, tableCapacity, isVirtual);

        // Then
        //verify(restaurantServices).addTable(eq(restaurantView), any());
        //verify(restaurantServices.tableBuilder()).number(eq(tableNumber));
        //verify(restaurantServices.tableBuilder()).capacity(eq(tableCapacity));
        //verify(restaurantServices.tableBuilder()).visibility(eq(true));
    }

    @Test
    public void shouldCreateNormalTable() {
        // Given
        boolean isVirtual = false;

        // When
        underTest.initialize(null, null);
        //underTest.createTable(0, 0, isVirtual);

        // Then
        //verify(restaurantServices.tableBuilder()).type(eq(NORMAL));
    }

    @Test
    public void shouldCreateVirtualTable() {
        // Given
        boolean isVirtual = true;

        // When
        underTest.initialize(null, null);
        //underTest.createTable(0, 0, isVirtual);

        // Then
        //verify(restaurantServices.tableBuilder()).type(eq(VIRTUAL));
    }
}
