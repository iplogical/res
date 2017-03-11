package com.inspirationlogical.receipt.utility;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Resources {

    PRINTER("printer");

    private final ResourceBundle resourceBundle;
    private static final String DEFAULT_LOCATION = "properties.";

    Resources(String bundleFile) {
        String location = DEFAULT_LOCATION + bundleFile;
        Locale l =Locale.forLanguageTag("hu");
        resourceBundle = ResourceBundle.getBundle(location,l );
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }
}
