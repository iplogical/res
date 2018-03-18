package com.inspirationlogical.receipt.corelib.frontend.view;

import com.inspirationlogical.receipt.corelib.frontend.application.MainStage;
import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ResourceBundle;

@Component
public class ViewLoader {

//    private FXMLLoaderProvider fxmlLoaderProvider;
//
//    @Inject
//    public ViewLoader(FXMLLoaderProvider fxmlLoaderProvider) {
//        this.fxmlLoaderProvider = fxmlLoaderProvider;
//    }

//    public Node loadView(String viewPath) {
//        Parent root = null;
//
//        FXMLLoader loader = getLoader(viewPath);
//        loader.setResources(MainStage.getResourcesProvider().getResources().getBundle());
//
//        try {
//            root = loader.load();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//
//        return root;
//    }
//
    public Node loadView(Controller controller) {
        Parent root = (Parent) controller.getRootNode();

        if (root == null) {
            FXMLLoader loader = getLoader(controller.getViewPath());
            loader.setController(controller);
            ResourceBundle resourceBundle = MainStage.getResourcesProvider().getResources().getBundle();
            loader.setResources(resourceBundle);
            try {
                root = loader.load();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return root;
    }

    private FXMLLoader getLoader(String viewPath) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewPath));
//        loader.setControllerFactory(springContext::getBean);
        return loader;
    }
//
//    public Node loadViewIntoScene(Controller controller) {
//        Parent root = (Parent) loadView(controller);
//        MainStage.getStageProvider().getStage().getScene().setRoot(root);
//        return root;
//    }
}
