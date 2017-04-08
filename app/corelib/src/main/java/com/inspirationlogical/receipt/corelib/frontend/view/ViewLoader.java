package com.inspirationlogical.receipt.corelib.frontend.view;

import java.io.IOException;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.application.MainStage;
import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.utility.Resources;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

public class ViewLoader {

    private FXMLLoaderProvider fxmlLoaderProvider;

    @Inject
    public ViewLoader(FXMLLoaderProvider fxmlLoaderProvider) {
        this.fxmlLoaderProvider = fxmlLoaderProvider;
    }

    public Node loadView(Controller controller) {
        Parent root = (Parent) controller.getRootNode();

        if (root == null) {
            FXMLLoader loader = fxmlLoaderProvider.getLoader(controller.getViewPath());
            loader.setController(controller);
            loader.setResources(Resources.UI.getBundle());
            try {
                root = loader.load();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return root;
    }

    public Node loadViewIntoScene(Controller controller) {
        Parent root = (Parent) loadView(controller);

        MainStage.getProvider().getStage().getScene().setRoot(root);

        return root;
    }
}
