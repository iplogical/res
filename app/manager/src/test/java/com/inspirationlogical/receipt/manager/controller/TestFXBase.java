package com.inspirationlogical.receipt.manager.controller;

import com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsFxmlView;
import com.inspirationlogical.receipt.manager.utility.AbstractUtils;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.TimeoutException;

public abstract class TestFXBase extends ApplicationTest {

    private static boolean isApplicationStarted = false;

    @BeforeClass
    public static void launchApplication() throws Exception {
        if(!isApplicationStarted) {
            isApplicationStarted = true;
            new BuildTestSchema().buildTestSchema();
            System.setProperty("java.awt.headless", "false");
            System.setProperty("spring.profiles.active", "test");
            AbstractJavaFxApplicationSupport.setSavedInitialView(GoodsFxmlView.class);
            ApplicationTest.launch(ManagerApp.class);
        }
    }

    @Before
    public void setUpClass() throws Exception {
        AbstractUtils.setRobot(this);
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

    @AfterClass
    public static void waitAfterClass() throws Exception {
        Thread.sleep(2000);
    }

    public <T extends Node> T find(final String query) {
        return (T) lookup(query).queryAll().iterator().next();
    }
}
