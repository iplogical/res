package com.inspirationlogical.receipt.corelib.utility;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

class NoLocaleTag
{
}

public enum Resources {

    PRINTER("printer"),
    WAITER("waiter"),
    MANAGER("manager"),
    CONFIG("appconfig", new NoLocaleTag());

    private final ResourceBundle resourceBundle;

    private static class Defaults {
        private static final String DEFAULT_LOCATION = "properties.";
        private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("hu");
    }

    Resources(String bundleFile) {
        String location = Defaults.DEFAULT_LOCATION + bundleFile;
        resourceBundle = ResourceBundle.getBundle(location, Defaults.DEFAULT_LOCALE, new UTF8Control());
    }

    Resources(String bundleFile,@SuppressWarnings("unused") NoLocaleTag t) {
        String location = Defaults.DEFAULT_LOCATION + bundleFile;
        resourceBundle = ResourceBundle.getBundle(location);
    }

    public String getString(String key) {
        try {
            return resourceBundle.getString(key);
        } catch(MissingResourceException e){
            return key;
        }
    }

    public ResourceBundle getBundle() {
        return resourceBundle;
    }
}
