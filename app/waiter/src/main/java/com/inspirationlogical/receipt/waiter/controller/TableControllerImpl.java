package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;
import com.inspirationlogical.receipt.waiter.viewstate.TableViewState;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showNode;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class TableControllerImpl implements TableController {

    private static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";
    private static final String CONSUMED_VIEW_PATH = "/view/fxml/ConsumedTable.fxml";

    @FXML
    Label root;
    @FXML
    AnchorPane container;
    @FXML
    StackPane host;
    @FXML
    Label name;
    @FXML
    Label guests;
    @FXML
    Label number;
    @FXML
    Label capacity;
    @FXML
    ImageView note;
    @FXML
    ImageView meeple;
    @FXML
    Label hosted;

    @Inject
    private ViewLoader viewLoader;

    private RestaurantController restaurantController;

    private RetailService retailService;

    private TableView tableView;

    private TableViewState tableViewState;

    @Inject
    public TableControllerImpl(RestaurantController restaurantController, RetailService retailService) {
        this.restaurantController = restaurantController;
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
        if (tableView.isConsumer()) {
            consumeTables();
        }
    }

    private void initVisual() {
        host.setPrefWidth(tableView.getDimension().getWidth());
        host.setPrefHeight(tableView.getDimension().getHeight());
    }

    @Override
    public void consumeTables() {
        releaseConsumed();
        tableView.getConsumedTables().forEach(view -> {
            StackPane stackPane = (StackPane) viewLoader.loadView(CONSUMED_VIEW_PATH);
            stackPane.setPrefWidth((int) view.getDimension().getWidth());
            stackPane.setPrefHeight((int) view.getDimension().getHeight());
            Point2D position = view.getPosition().subtract(tableView.getPosition());
            stackPane.setLayoutX((int) position.getX());
            stackPane.setLayoutY((int) position.getY());
            Label label = (Label) stackPane.lookup("#number");
            label.setText(valueOf(view.getNumber()));
            container.getChildren().add(stackPane);
        });
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
        if (tableView.isVisible()) {
            initVisual();

            name.setText(tableView.getName());
            number.setText(valueOf(tableView.getNumber()));

            if (tableView.isConsumer()) {

                Integer tableGuests = tableView.getGuestCount();
                Integer tableCapacity = tableView.getCapacity();

                for(TableView view : tableView.getConsumedTables()) {
                    tableGuests += view.getGuestCount();
                    tableCapacity += view.getCapacity();
                }

                guests.setText(valueOf(tableGuests));
                capacity.setText(valueOf(tableCapacity));
            } else {
                number.setText(valueOf(tableView.getNumber()));
                guests.setText(valueOf(tableView.getGuestCount()));
                capacity.setText(valueOf(tableView.getCapacity()));
            }

            if (isEmpty(tableView.getNote())) {
                note.setVisible(false);
            } else {
                note.setVisible(true);
            }

            if (tableView.isHost()) {
                meeple.setVisible(true);
                hosted.setText(valueOf(tableView.getHostedTables().size()));
            } else {
                meeple.setVisible(false);
                hosted.setText(EMPTY);
            }

            CSSUtilities.setBackgroundColor(tableViewState.isOpen(), host);

            showNode(root, tableView.getPosition());
        } else {
            hideNode(root);
        }
    }

    @Override
    public void releaseConsumed() {
        container.getChildren().clear();
        container.getChildren().add(host);
    }

    @Override
    public void openTable(Control control) {
        retailService.openTable(tableView);
        updateNode();
    }

    @Override
    public void deselectTable() {
        tableViewState.setSelected(false);
        CSSUtilities.setBorderColor(tableViewState.isSelected(), host);
    }

    @FXML
    public void onTableClicked(MouseEvent event) {
        if (isContextMenuOpen() || tableViewState.getRestaurantViewState().getMotionViewState().getMovableProperty().getValue()) {
            for (TableView view : tableView.getConsumedTables()) {
                Point2D delta = new Point2D(root.getLayoutX(), root.getLayoutY()).subtract(tableView.getPosition());
                restaurantController.moveTable(view, view.getPosition().add(delta));
            }
            restaurantController.moveTable(this);
            return;
        }

        if(tableViewState.getRestaurantViewState().getConfigurable().getValue()) {
            tableViewState.setSelected(!tableViewState.isSelected());
            restaurantController.selectTable(this, tableViewState.isSelected());
            CSSUtilities.setBorderColor(tableViewState.isSelected(), host);
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
