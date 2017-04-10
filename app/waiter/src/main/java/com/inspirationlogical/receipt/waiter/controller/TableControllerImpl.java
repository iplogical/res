package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculatePopupPosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.Orientation;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;
import com.inspirationlogical.receipt.waiter.viewstate.TableViewState;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class TableControllerImpl implements TableController {

    public static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";
    private static double TABLE_SIZE_UNIT = 20.0;
    private static double TABLE_SIZE_MIN = 4 * TABLE_SIZE_UNIT;

    @FXML
    AnchorPane tablesTab;

    @FXML
    Label root;

    @FXML
    VBox vBox;

    @FXML
    Label name;

    @FXML
    Label number;

    @FXML
    Label guests;

    @FXML
    Label capacity;

    @FXML
    ImageView note;

    @Inject
    private ViewLoader viewLoader;

    private Popup tableSettingsForm;

    private RestaurantController restaurantController;

    private TableSettingsFormController tableSettingsFormController;

    private RestaurantService restaurantService;

    private RetailService retailService;

    private TableView tableView;

    private TableViewState tableViewState;

    @Inject
    public TableControllerImpl(RestaurantController restaurantController,
                               TableSettingsFormController tableSettingsFormController,
                               RestaurantService restaurantService,
                               RetailService retailService) {
        this.restaurantController = restaurantController;
        this.tableSettingsFormController = tableSettingsFormController;
        this.restaurantService = restaurantService;
        this.retailService = retailService;
    }

    public void setView(TableView tableView) {
        this.tableView = tableView;
        this.tableViewState = new TableViewState(restaurantController.getViewState(), tableView);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root, tableViewState.getRestaurantViewState().getMotionViewState());
        initVisual();
        updateNode();
    }

    private void initVisual() {
        vBox.setPrefWidth(TABLE_SIZE_UNIT * roundCapacity());
        vBox.setPrefHeight(TABLE_SIZE_MIN);
    }

    private int roundCapacity() {
        int capacity = tableView.getTableCapacity();
        return capacity <= 4 ? 4 : ((capacity + 1) / 2) * 2;
    }

    private void rotateRoot() {
        initVisual();
        int width = (int) vBox.getPrefWidth();
        int height = (int) vBox.getPrefHeight();
        if (tableView.getOrientation() != Orientation.HORIZONTAL) {
            vBox.setPrefWidth(height);
            vBox.setPrefHeight(width);
        } else {
            vBox.setPrefWidth(width);
            vBox.setPrefHeight(height);
        }
    }

    @Override
    public TableView getView() {
        return tableView;
    }

    @Override
    public Control getRoot() {
        return root;
    }

    @Override
    public ViewState getViewState() {
        return tableViewState;
    }

    @Override
    public void updateNode() {
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getTableNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getTableCapacity()));
        if(isEmpty(tableView.getNote())) {
            note.setVisible(false);
        } else {
            note.setVisible(true);
        }
        CSSUtilities.setBackgroundColor(tableViewState.isOpen(), vBox);
        rotateRoot();
        showNode(root, tableView.getPosition());
    }

    @Override
    public void showTableSettingsForm(Control control) {
        tableSettingsForm = new Popup();
        tableSettingsForm.getContent().add(viewLoader.loadView(tableSettingsFormController));
        tableSettingsFormController.loadTableSettings(this);

        Point2D position = calculatePopupPosition(control, (Pane)root.getParent());

        showPopup(tableSettingsForm, tableSettingsFormController, root, position);
    }

    @Override
    public void setTable(String name, int guestCount, String note) {
        restaurantService.setTableName(tableView, name);
        restaurantService.setGuestCount(tableView, guestCount);
        restaurantService.addTableNote(tableView, note);
        tableSettingsForm.hide();
        restaurantController.updateRestaurant();
    }

    @Override
    public void openTable(Control control) {
        retailService.openTable(tableView);
        updateNode();
    }

    @Override
    public void deselectTable() {
        tableViewState.setSelected(false);
        CSSUtilities.setBorderColor(tableViewState.isSelected(), vBox);
    }

    @FXML
    public void onTableClicked(MouseEvent event) {
        if (isContextMenuOpen() || tableViewState.getRestaurantViewState().getMotionViewState().getMovableProperty().getValue()) {
            restaurantController.moveTable(this);
            return;
        }

        if (tableView.isVirtual() || !tableView.isOpen()) {
            return;
        }

        if(tableViewState.getRestaurantViewState().getConfigurable().getValue()) {
            tableViewState.setSelected(!tableViewState.isSelected());
            restaurantController.selectTable(this, tableViewState.isSelected());
            CSSUtilities.setBorderColor(tableViewState.isSelected(), vBox);
        } else {
            if(!tableView.isOpen()) {
                return;
            }
            SaleController saleController = WaiterRegistry.getInstance(SaleController.class);
            saleController.setTableView(tableView);
            saleController.updateNode();
            viewLoader.loadViewIntoScene(saleController);
        }
    }

    private boolean isContextMenuOpen() {
        return root.getContextMenu() != null && root.getContextMenu().isShowing();
    }

    @Override
    public String getViewPath() {
        return TABLE_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
