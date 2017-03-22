package com.inspirationlogical.receipt.corelib.utility;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class NoLocaleTag
{
}

public enum Resources {


    PRINTER("printer"),
    UI("ui"),
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
            return new String(resourceBundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
        } catch(MissingResourceException | UnsupportedEncodingException e){
            return key;
        }
    }

    public String getStringISO88591(String key) {
        try {
            return resourceBundle.getString(key);
        } catch(MissingResourceException e){
            return key;
        }
    }
}
