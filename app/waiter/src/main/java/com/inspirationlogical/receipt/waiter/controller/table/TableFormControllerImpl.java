package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.service.table.TableServiceConfig;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addFormDragAndDrop;

@Component
public class TableFormControllerImpl implements TableFormController {

    private static final Integer NORMAL_TABLE_DEFAULT_CAPACITY = 4;
    private static final Integer VIRTUAL_TABLE_DEFAULT_CAPACITY = 1;
    private static final Integer TABLE_DEFAULT_WIDTH = 80;
    private static final Integer TABLE_DEFAULT_HEIGHT = 80;

    @FXML
    private VBox root;
    @FXML
    private Label title;
    @FXML
    private TextField tableName;
    @FXML
    private TextField guestCount;
    @FXML
    private TextArea tableNote;
    @FXML
    private TextField tableNumber;
    @FXML
    private TextField tableCapacity;
    @FXML
    private TextField width;
    @FXML
    private TextField height;

    private TableController tableController = null;

    @Autowired
    private TableConfigurationController tableConfigurationController;

    @Autowired
    TableServiceConfig tableServiceConfig;

    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
        addFormDragAndDrop(root);
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        try {
            TableParams tableParams = buildTableParams();
            if (tableController == null) {
                tableConfigurationController.addTable(tableParams);
            } else {
                tableConfigurationController.editTable(tableController, tableParams);
                tableController = null;
            }
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(getRootNode(),
                    WaiterResources.WAITER.getString("TableForm.NumberFormatError"));
        }
    }

    private TableParams buildTableParams() throws NumberFormatException {
        int tableNumber = Integer.valueOf(this.tableNumber.getText());
        int tableCapacity = Integer.valueOf(this.tableCapacity.getText());
        int tableGuestCount = guestCount.getText().equals("") ? 0 : Integer.valueOf(guestCount.getText());
        Dimension2D dimension = new Dimension2D(Integer.valueOf(width.getText()), Integer.valueOf(height.getText()));
        return TableParams.builder()
                .name(tableName.getText())
                .number(tableNumber)
                .note(tableNote.getText())
                .guestCount(tableGuestCount)
                .capacity(tableCapacity)
                .width((int) dimension.getWidth())
                .height((int) dimension.getHeight())
                .build();
    }

    @FXML
    public void onCancel(MouseEvent event) {
        tableController = null;
        tableConfigurationController.hideTableForm();
    }

    @Override
    public void createTableForm(TableType tableType) {
        title.setText(resourceBundle.getString("TableForm.Create"));
        tableNumber.setText(String.valueOf(tableServiceConfig.getFirstUnusedNumber()));
        guestCount.setText(tableType == TableType.NORMAL ? "0" : "1");
        tableCapacity.setText(tableType == TableType.NORMAL ? NORMAL_TABLE_DEFAULT_CAPACITY.toString() : VIRTUAL_TABLE_DEFAULT_CAPACITY.toString());
        tableName.clear();
        tableNote.clear();
        width.setText(TABLE_DEFAULT_WIDTH.toString());
        height.setText(TABLE_DEFAULT_HEIGHT.toString());
    }

    @Override
    public void loadTableForm(TableController tableController, TableType tableType) {
        this.tableController = tableController;
        title.setText(resourceBundle.getString("TableForm.Edit"));
        TableView tableView = tableController.getTableView();
        tableNumber.setText(String.valueOf(tableView.getNumber()));
        tableCapacity.setText(String.valueOf(tableView.getCapacity()));
        tableName.setText(tableView.getName());
        guestCount.setText(String.valueOf(tableView.getGuestCount()));
        tableNote.setText(tableView.getNote());
        width.setText(String.valueOf(tableView.getWidth()));
        height.setText(String.valueOf(tableView.getHeight()));
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
