package com.inspirationlogical.receipt.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import javafx.embed.swing.JFXPanel;

@RunWith(PowerMockRunner.class)
public class ContextMenuControllerTest {

    @Mock
    private RestaurantController restaurantController;

    private JFXPanel jfxPanel;

    private ContextMenuControllerImpl underTest;

    @Before
    public void setUp() throws Exception {
        jfxPanel = new JFXPanel();
        underTest = new ContextMenuControllerImpl(restaurantController);
    }

    @Test
    public void shouldInitialize() {
        // Given

        // When
        underTest.initialize(null, null);

        // Then
    }
}
