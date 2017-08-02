package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.frontend.application.MainStage;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.utility.ClickUtils;
import com.inspirationlogical.receipt.waiter.utility.PayUtils;
import com.inspirationlogical.receipt.waiter.utility.SaleUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.TimeoutException;

import static com.inspirationlogical.receipt.waiter.registry.WaiterRegistry.getInstance;


/**
 * Created by TheDagi on 2017. 05. 09..
 */
public abstract class TestFXBase extends ApplicationTest {

    @BeforeClass
    public static void launchApplication() throws Exception {
        ApplicationTest.launch(WaiterApp.class);
    }

    @Before
    public void setUpClass() throws Exception {
        ClickUtils.setRobot(this);
        SaleUtils.setRobot(this);
        PayUtils.setRobot(this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }

    @After
    public void afterEachTest() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }
}
