package com.inspirationlogical.receipt.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.inspirationlogical.receipt.application.Main;

@RunWith(PowerMockRunner.class)
public class ContextMenuControllerTest {

    @Mock
    private RestaurantController restaurantController;

    private Main main;

    private ContextMenuControllerImpl underTest;

    @Before
    public void setUp() throws Exception {
        main = new Main();
        underTest = new ContextMenuControllerImpl(restaurantController);
    }

    @Test
    public void shouldInitialize() {
        // Given

        // When
        //underTest.initialize(null, null);

        // Then
    }
}
