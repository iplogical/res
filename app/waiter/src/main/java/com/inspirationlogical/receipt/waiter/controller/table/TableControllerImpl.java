package com.inspirationlogical.receipt.waiter.controller.table;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showNode;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class TableControllerImpl implements TableController {

    private static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";
    private static final String CONSUMED_VIEW_PATH = "/view/fxml/ConsumedTable.fxml";

    @FXML
    Label rootTable;
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
        addDragAndDrop(rootTable, tableViewState.getRestaurantViewState().getMotionViewState());
        setPrefferedSize();
        updateNode();
        if (tableView.isConsumer()) {
            consumeTables();
        }
    }

    private void setPrefferedSize() {
        host.setPrefWidth(tableView.getDimension().getWidth());
        host.setPrefHeight(tableView.getDimension().getHeight());
    }

    @Override
    public void consumeTables() {
        releaseConsumed();
        tableView.getConsumedTables().forEach(view -> {
            StackPane consumedRoot = (StackPane) viewLoader.loadView(CONSUMED_VIEW_PATH);
            consumedRoot.setPrefWidth((int) view.getDimension().getWidth());
            consumedRoot.setPrefHeight((int) view.getDimension().getHeight());
            Point2D position = view.getPosition().subtract(tableView.getPosition());
            consumedRoot.setLayoutX((int) position.getX());
            consumedRoot.setLayoutY((int) position.getY());

            Label consumedNumber = (Label) consumedRoot.lookup("#number");
            Label consumedHosted = (Label) consumedRoot.lookup("#hosted");
            ImageView consumedMeeple = (ImageView) consumedRoot.lookup("#meeple");

            consumedNumber.setText(valueOf(view.getNumber()));

            int hostedSize = view.getHostedTables().size();
            if (hostedSize > 0) {
                consumedMeeple.setVisible(true);
                consumedHosted.setVisible(true);
                consumedHosted.setText(valueOf(hostedSize));
            } else {
                consumedMeeple.setVisible(false);
                consumedHosted.setVisible(false);
            }
            container.getChildren().add(consumedRoot);
        });
    }

    @Override
    public TableView getView() {
        return tableView;
    }

    @Override
    public Control getRoot() {
        return rootTable;
    }

    @Override
    public ViewState getViewState() {
        return tableViewState;
    }

    @Override
    public void updateNode() {
        if (tableView.isVisible()) {
            setPrefferedSize();

            name.setText(tableView.getName());

            updateHostInfo();

            if (tableView.isConsumer()) {
                consumeTables();
                Integer tableGuests = tableView.getGuestCount();
                Integer tableCapacity = tableView.getCapacity();
                for(TableView consumed : tableView.getConsumedTables()) {
                    tableGuests += consumed.getGuestCount();
                    tableCapacity += consumed.getCapacity();
                }
                guests.setText(valueOf(tableGuests));
                capacity.setText(valueOf(tableCapacity));
            } else {
                guests.setText(valueOf(tableView.getGuestCount()));
                capacity.setText(valueOf(tableView.getCapacity()));
            }

            updateNote();
            CSSUtilities.setBackgroundColor(tableViewState.isOpen(), host);
            showNode(rootTable, tableView.getPosition());
        } else {
            if (tableView.isConsumed()) {
                restaurantController.getTableController(tableView.getConsumer()).updateNode();
            }
            hideNode(rootTable);
        }
    }

    private void updateHostInfo() {
        if (tableView.isHost()) {
            meeple.setVisible(true);
            hosted.setText(valueOf(tableView.getHostedTables().size()));
        } else {
            meeple.setVisible(false);
            hosted.setText(EMPTY);
        }
        if (tableView.isHosted()) {
            number.setText(valueOf(tableView.getHost().getNumber()));
        } else {
            number.setText(valueOf(tableView.getNumber()));
        }
    }

    private void updateNote() {
        if (isEmpty(tableView.getNote())) {
            note.setVisible(false);
        } else {
            note.setVisible(true);
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
                Point2D delta = new Point2D(rootTable.getLayoutX(), rootTable.getLayoutY()).subtract(tableView.getPosition());
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
            saleController.enterSaleView();
            viewLoader.loadViewIntoScene(saleController);
        }
    }

    private boolean isContextMenuOpen() {
        return rootTable.getContextMenu() != null && rootTable.getContextMenu().isShowing();
    }

    @Override
    public String getViewPath() {
        return TABLE_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return rootTable;
    }
}
