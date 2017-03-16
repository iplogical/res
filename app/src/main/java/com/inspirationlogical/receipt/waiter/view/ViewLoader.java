package com.inspirationlogical.receipt.waiter.view;

import java.io.IOException;

import com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;

public class ViewLoader {

    public static Node loadView(String viewPath, Initializable controller) {
        FXMLLoader loader = FXMLLoaderProvider.getLoader(viewPath);
        loader.setController(controller);
        Node view = null;

        try {
            view = loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return view;
    }

    public static Node loadViewHidden(String viewPath, Initializable controller) {
        Node view = loadView(viewPath, controller);
        if (view != null) view.setVisible(false);

        return view;
    }
}
