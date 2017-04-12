package com.inspirationlogical.receipt.corelib.frontend.application;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class MainStage {

    public static final String APP_TITLE = "Receipt";
    public static final int APP_WIDTH = 1024;
    public static final int APP_HEIGHT = 768;

    @Getter
    private static StageProvider stageProvider;

    @Getter @Setter
    private static ResourcesProvider resourcesProvider;

    public static void setStageProvider(StageProvider stageProvider) {
        MainStage.stageProvider = stageProvider;

        Stage stage = MainStage.stageProvider.getStage();
        stage.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });
    }
}
