package com.inspirationlogical.receipt.corelib.utility;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class NoLocaleTag
{
}

public enum Resources {


    PRINTER("printer"),
    CONFIG("appconfig",new NoLocaleTag());

    private final ResourceBundle resourceBundle;
    private static final String DEFAULT_LOCATION = "properties.";

    Resources(String bundleFile) {
        String location = DEFAULT_LOCATION + bundleFile;
        Locale l =Locale.forLanguageTag("hu");
        resourceBundle = ResourceBundle.getBundle(location,l );
    }

    Resources(String bundleFile,@SuppressWarnings("unused") NoLocaleTag t) {
        String location = DEFAULT_LOCATION + bundleFile;
        resourceBundle = ResourceBundle.getBundle(location);
    }


    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        }catch(MissingResourceException e){
            return key;
        }
    }
}
