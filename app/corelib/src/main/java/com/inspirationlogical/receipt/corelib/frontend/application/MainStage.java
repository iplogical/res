package com.inspirationlogical.receipt.corelib.frontend.application;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;

public class MainStage {

    public static final String APP_TITLE = "Receipt";
    public static final int APP_WIDTH = 1024;
    public static final int APP_HEIGHT = 768;

    @Getter
    private static StageProvider provider;

    public static void setProvider(StageProvider provider) {
        MainStage.provider = provider;

        Stage stage = MainStage.provider.getStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
    }
}
