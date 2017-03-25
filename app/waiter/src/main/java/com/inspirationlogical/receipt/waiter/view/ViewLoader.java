package com.inspirationlogical.receipt.waiter.view;

import java.io.IOException;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.controller.Controller;
import com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class ViewLoader {

    public static Node loadView(String viewPath, Controller controller) {
        FXMLLoader loader = FXMLLoaderProvider.getLoader(viewPath);
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
