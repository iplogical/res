package com.inspirationlogical.receipt.corelib.frontend.view;

import java.io.IOException;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.utility.Resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ViewLoader {

    private FXMLLoaderProvider fxmlLoaderProvider;

    @Inject
    public ViewLoader(FXMLLoaderProvider fxmlLoaderProvider) {
        this.fxmlLoaderProvider = fxmlLoaderProvider;
    }

    public Node loadView(String viewPath, Controller controller) {
        FXMLLoader loader = fxmlLoaderProvider.getLoader(viewPath);
        loader.setController(controller);
        loader.setResources(Resources.UI.getBundle());
        Node view = null;

        try {
            view = loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return view;
    }
}