package com.inspirationlogical.receipt.corelib.frontend.application;

import lombok.Getter;
import lombok.Setter;

public class MainStage {

    public static final String APP_TITLE = "Receipt";
    public static final int APP_WIDTH = 1024;
    public static final int APP_HEIGHT = 768;

    @Getter
    @Setter
    private static StageProvider provider;
}
