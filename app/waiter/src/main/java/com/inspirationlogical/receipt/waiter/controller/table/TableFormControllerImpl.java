package com.inspirationlogical.receipt.waiter.controller.table;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

@Singleton
public class TableFormControllerImpl implements TableFormController {

    private static final String TABLE_FORM_VIEW_PATH = "/view/fxml/TableForm.fxml";
    private static final Integer NORMAL_TABLE_DEFAULT_CAPACITY = 4;
    private static final Integer VIRTUAL_TABLE_DEFAULT_CAPACITY = 1;
    private static final Integer TABLE_DEFAULT_WIDTH = 80;
    private static final Integer TABLE_DEFAULT_HEIGHT = 80;

    @FXML private VBox root;
    @FXML private Label title;
    @FXML private TextField tableName;
    @FXML private TextField guestCount;
    @FXML private TextArea tableNote;
    @FXML private TextField tableNumber;
    @FXML private TextField tableCapacity;
    @FXML private TextField width;
    @FXML private TextField height;

    private TableController tableController = null;

    private TableConfigurationController tableConfigurationController;

    private RestaurantService restaurantService;

    private ResourceBundle resourceBundle;

    @Inject
    public TableFormControllerImpl(TableConfigurationController tableConfigurationController,
                                   RestaurantService restaurantService) {
        this.tableConfigurationController = tableConfigurationController;
        this.restaurantService = restaurantService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
        addDragAndDrop(root);
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
                    Resources.WAITER.getString("TableForm.NumberFormatError"));
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
                .dimension(dimension)
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
        tableNumber.setText(String.valueOf(restaurantService.getFirstUnusedNumber()));
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
        TableView tableView = tableController.getView();
        if (tableView.canBeHosted() && tableView.isHosted()) {
            tableNumber.setText(String.valueOf(tableView.getHost().getNumber()));
        } else {
            tableNumber.setText(String.valueOf(tableView.getNumber()));
        }
        tableCapacity.setText(String.valueOf(tableView.getCapacity()));
        tableName.setText(tableView.getName());
        guestCount.setText(String.valueOf(tableView.getGuestCount()));
        tableNote.setText(tableView.getNote());
        width.setText(String.valueOf((int) tableView.getDimension().getWidth()));
        height.setText(String.valueOf((int) tableView.getDimension().getHeight()));
    }

    @Override
    public String getViewPath() {
        return TABLE_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
