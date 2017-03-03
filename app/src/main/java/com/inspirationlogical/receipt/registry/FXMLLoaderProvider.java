package com.inspirationlogical.receipt.registry;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.fxml.FXMLLoader;

public class FXMLLoaderProvider {

    private static final Injector injector = Guice.createInjector(new Registry());

    public static FXMLLoader getLoader(String viewPath) {
        FXMLLoader loader = new FXMLLoader(FXMLLoaderProvider.class.getResource(viewPath));
        loader.setControllerFactory(injector::getInstance);
        return loader;
    }
}
