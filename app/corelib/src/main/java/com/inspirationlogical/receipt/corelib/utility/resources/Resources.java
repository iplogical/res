package com.inspirationlogical.receipt.corelib.utility.resources;

import com.inspirationlogical.receipt.corelib.utility.UTF8Control;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Resources {

    public static final ResourceBundleWrapper PRINTER;
    public static final ResourceBundleWrapper CONFIG;

    public static final String DEFAULT_LOCATION = "properties.";
    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("hu");


    static {
        String location = DEFAULT_LOCATION + "printer";
        PRINTER = new ResourceBundleWrapper(ResourceBundle.getBundle(location, DEFAULT_LOCALE, new UTF8Control()));
        location = DEFAULT_LOCATION + "appconfig";
        CONFIG = new ResourceBundleWrapper(ResourceBundle.getBundle(location, DEFAULT_LOCALE, new UTF8Control()));
    }
}
