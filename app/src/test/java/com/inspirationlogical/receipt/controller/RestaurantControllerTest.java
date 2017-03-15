package com.inspirationlogical.receipt.controller;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.inspirationlogical.receipt.service.RestaurantServices;

import javafx.embed.swing.JFXPanel;
import javafx.scene.layout.AnchorPane;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantControllerTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private AnchorPane anchorPane;

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
        jfxPanel = new JFXPanel();
        underTest = new RestaurantControllerImpl(restaurantServices, contextMenuController, addTableFormController);
        underTest.layout = anchorPane;
    }

    @Test
    public void shouldInitializeController() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then
        verify(restaurantServices).getActiveRestaurant();
        verify(restaurantServices).getTables(any());
        verify(anchorPane, times(2)).getChildren();
    }
}
