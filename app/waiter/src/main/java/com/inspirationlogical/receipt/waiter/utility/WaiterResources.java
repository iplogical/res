package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.utility.UTF8Control;
import com.inspirationlogical.receipt.corelib.utility.resources.ResourceBundleWrapper;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

import java.util.ResourceBundle;

public class WaiterResources extends Resources {

    public static final ResourceBundleWrapper WAITER;

    static {
        String location = Resources.DEFAULT_LOCATION + "waiter";
        WAITER = new ResourceBundleWrapper(ResourceBundle.getBundle(location, Resources.DEFAULT_LOCALE, new UTF8Control()));
    }
}
