package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.model.enums.TableType.NORMAL;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.VIRTUAL;
import static com.inspirationlogical.receipt.waiter.controller.TableFormControllerImpl.TABLE_FORM_VIEW_PATH;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
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

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import com.inspirationlogical.receipt.waiter.view.NodeUtility;
import com.inspirationlogical.receipt.waiter.view.PressAndHoldHandler;
import com.inspirationlogical.receipt.waiter.view.ViewLoader;

import javafx.geometry.Point2D;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

@PrepareForTest({PressAndHoldHandler.class, ViewLoader.class, NodeUtility.class})
@RunWith(PowerMockRunner.class)
public class RestaurantControllerTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private AnchorPane tables;

    @Mock
    private VBox contextMenu;

    @Mock
    private VBox addTableForm;

    @Mock
    private TableFormController tableFormController;

    @Mock
    private RestaurantServices restaurantServices;

    @Mock
    private RestaurantView restaurantView;

    @Mock
    private TableView tableView;

    private Main main;

    private RestaurantControllerImpl underTest;

    @Before
    public void setUp() throws Exception {
        when(restaurantServices.getActiveRestaurant()).thenReturn(restaurantView);

        mockStatic(PressAndHoldHandler.class);
        mockStatic(ViewLoader.class);
        when(ViewLoader.loadViewHidden(TABLE_FORM_VIEW_PATH, tableFormController)).thenReturn(addTableForm);

        main = new Main();
        underTest = new RestaurantControllerImpl(restaurantServices, tableFormController);
        //underTest.tablesTab = tablesTab;
    }

    @Test
    public void shouldInitializeContextMenu() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then
        verify(tables.getChildren()).add(contextMenu);


        verifyStatic(times(1));
//        PressAndHoldHandler.addPressAndHold(eq(futykos), eq(contextMenu), any());
    }

    @Test
    public void shouldInitializeAddTableForm() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then
        verify(tables.getChildren()).add(addTableForm);

        verifyStatic(times(1));
        ViewLoader.loadViewHidden(eq(TABLE_FORM_VIEW_PATH), eq(tableFormController));
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

        mockStatic(NodeUtility.class);
        when(NodeUtility.getNodePosition(contextMenu)).thenReturn(position);

        // When
        underTest.initialize(null, null);
        underTest.showAddTableForm(position);

        // Then
        verifyStatic(times(1));
        NodeUtility.showNode(eq(addTableForm), eq(position));
    }

    @Test
    public void shouldCreateTable() {
        // Given
        Point2D position = new Point2D(0, 0);
        int tableNumber = 5;
        int tableCapacity = 4;
        boolean isVirtual = false;

        when(restaurantServices.addTable(restaurantView, TableType.NORMAL, tableNumber)).thenReturn(tableView);

        mockStatic(NodeUtility.class);
        when(NodeUtility.getNodePosition(addTableForm)).thenReturn(position);

        // When
        underTest.initialize(null, null);
        underTest.createTable(tableNumber, tableCapacity, isVirtual);

        // Then
        verify(restaurantServices).setTableCapacity(eq(tableView), eq(tableCapacity));
        verify(restaurantServices).moveTable(eq(tableView), eq(position));

        verifyStatic(times(1));
        NodeUtility.hideNode(eq(addTableForm));

        verifyStatic(times(1));
//        PressAndHoldHandler.addPressAndHold(eq(futykos), eq(contextMenu), any());
    }

    @Test
    public void shouldCreateNormalTable() {
        // Given
        int tableNumber = 5;
        int tableCapacity = 4;
        boolean isVirtual = false;

        when(restaurantServices.addTable(restaurantView, TableType.NORMAL, tableNumber)).thenReturn(tableView);

        // When
        underTest.initialize(null, null);
        underTest.createTable(tableNumber, tableCapacity, isVirtual);

        // Then
        verify(restaurantServices).addTable(eq(restaurantView), eq(NORMAL), eq(tableNumber));
    }

    @Test
    public void shouldCreateVirtualTable() {
        // Given
        int tableNumber = 5;
        int tableCapacity = 4;
        boolean isVirtual = true;

        when(restaurantServices.addTable(restaurantView, TableType.VIRTUAL, tableNumber)).thenReturn(tableView);

        // When
        underTest.initialize(null, null);
        underTest.createTable(tableNumber, tableCapacity, isVirtual);

        // Then
        verify(restaurantServices).addTable(eq(restaurantView), eq(VIRTUAL), eq(tableNumber));
    }
}
