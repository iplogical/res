package com.inspirationlogical.receipt.utility;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Resources {

    PRINTER("printer");

    private final ResourceBundle resourceBundle;
    private static final String DEFAULT_LOCATION = "properties.";

    Resources(String bundleFile) {
        resourceBundle = ResourceBundle.getBundle(DEFAULT_LOCATION + bundleFile,
                Locale.forLanguageTag("hu"));
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }
}
