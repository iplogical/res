package com.inspirationlogical.receipt.corelib.utility.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleWrapper {

    private ResourceBundle resourceBundle;

    public ResourceBundleWrapper(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
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
