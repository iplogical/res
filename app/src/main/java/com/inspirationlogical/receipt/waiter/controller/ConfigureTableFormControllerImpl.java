package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;


import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.hideNode;

/**
 * Created by Bálint on 2017.03.21..
 */
@Singleton
public class ConfigureTableFormControllerImpl implements ConfigureTableFormController {

    public static final String CONFIGURE_TABLE_FORM_VIEW_PATH = "/view/fxml/ConfigureTableForm.fxml";

    @FXML
    private VBox root;

    @FXML
    private TextField tableName;

    @FXML
    private TextField guestNumber;

    private TableController tableController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root);
        guestNumber.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @Override
    public void loadConfigureTable(TableController tableController) {
        this.tableController = tableController;
        TableView tableView = tableController.getView();
        tableName.setText(tableView.getName());
        guestNumber.setText(String.valueOf(tableView.getGuestCount()));
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        tableController.configureTable(tableName.getText(), Integer.valueOf(guestNumber.getText()));
        hideNode(root);
    }

    @FXML
    public void onCancel(MouseEvent event) {
        hideNode(root);
    }
}

