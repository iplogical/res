package com.inspirationlogical.receipt.manager.utility;

import com.inspirationlogical.receipt.corelib.utility.UTF8Control;
import com.inspirationlogical.receipt.corelib.utility.resources.ResourceBundleWrapper;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

import java.util.ResourceBundle;

public class ManagerResources extends Resources {

    public static final ResourceBundleWrapper MANAGER;

    static {
        String location = Resources.DEFAULT_LOCATION + "manager";
        MANAGER = new ResourceBundleWrapper(ResourceBundle.getBundle(location, Resources.DEFAULT_LOCALE, new UTF8Control()));
    }

}
