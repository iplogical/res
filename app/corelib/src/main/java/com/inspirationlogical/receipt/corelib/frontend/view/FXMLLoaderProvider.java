package com.inspirationlogical.receipt.corelib.frontend.view;

import static com.google.inject.Guice.createInjector;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

import javafx.fxml.FXMLLoader;

public class FXMLLoaderProvider {

    private final Injector injector;

    @Inject
    public FXMLLoaderProvider(Module module) {
        this.injector = createInjector(module);
    }

    public FXMLLoader getLoader(String viewPath) {
        FXMLLoader loader = new FXMLLoader(FXMLLoaderProvider.class.getResource(viewPath));
        loader.setControllerFactory(injector::getInstance);
        return loader;
    }
}
