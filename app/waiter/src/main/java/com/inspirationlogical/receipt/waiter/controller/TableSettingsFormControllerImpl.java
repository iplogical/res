package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.hideNode;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

/**
 * Created by Bálint on 2017.03.21..
 */
@Singleton
public class TableSettingsFormControllerImpl implements TableSettingsFormController {

    public static final String TABLE_SETTINGS_FORM_VIEW_PATH = "/view/fxml/TableSettingsForm.fxml";

    @FXML
    private VBox root;

    @FXML
    private TextField tableName;

    @FXML
    private TextField guestNumber;

    @FXML
    private TextArea note;

    private TableController tableController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root);
        guestNumber.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @Override
    public void loadTableSettings(TableController tableController) {
        this.tableController = tableController;
        TableView tableView = tableController.getView();
        tableName.setText(tableView.getName());
        guestNumber.setText(String.valueOf(tableView.getGuestCount()));
        note.setText(tableView.getNote());
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        tableController.setTable(tableName.getText(), Integer.valueOf(guestNumber.getText()), note.getText());
    }

    @FXML
    public void onCancel(MouseEvent event) {
        hideNode(root);
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}

