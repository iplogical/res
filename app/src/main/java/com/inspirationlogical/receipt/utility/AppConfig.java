package com.inspirationlogical.receipt.utility;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
public class AppConfig {

    private final ResourceBundle resourceBundle;
    private static final String DEFAULT_LOCATION = "properties.";

    AppConfig(String bundleFile) {
        String location = DEFAULT_LOCATION + bundleFile;
        Locale l =Locale.forLanguageTag("hu");
        resourceBundle = ResourceBundle.getBundle(location,l );
    }

    public String getString(String key) {
        return resourceBundle.getString(key);
    }

}
