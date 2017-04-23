package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showNode;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;
import com.inspirationlogical.receipt.waiter.viewstate.TableViewState;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TableControllerImpl implements TableController {

    public static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";

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

    private RestaurantController restaurantController;

    private RestaurantService restaurantService;

    private RetailService retailService;

    private TableView tableView;

    private TableViewState tableViewState;

    @Inject
    public TableControllerImpl(RestaurantController restaurantController,
                               RestaurantService restaurantService,
                               RetailService retailService) {
        this.restaurantController = restaurantController;
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
        vBox.setPrefWidth(tableView.getDimension().getWidth());
        vBox.setPrefHeight(tableView.getDimension().getHeight());
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
        initVisual();
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getCapacity()));
        if(isEmpty(tableView.getNote())) {
            note.setVisible(false);
        } else {
            note.setVisible(true);
        }
        CSSUtilities.setBackgroundColor(tableViewState.isOpen(), vBox);
        showNode(root, tableView.getPosition());
    }

    @Override
    public void setTable(String name, int guestCount, String note) {
        restaurantService.setTableName(tableView, name);
        restaurantService.setGuestCount(tableView, guestCount);
        restaurantService.addTableNote(tableView, note);
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
