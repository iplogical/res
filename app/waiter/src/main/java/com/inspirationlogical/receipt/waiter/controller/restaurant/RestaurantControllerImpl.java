package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.contextmenu.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.contextmenu.RestaurantContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.controller.dailysummary.DailySummaryController;
import com.inspirationlogical.receipt.waiter.controller.dailysummary.DailySummaryFxmlView;
import com.inspirationlogical.receipt.waiter.controller.reservation.ReservationFxmlView;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.moveNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.waiter.utility.ConfirmMessage.showConfirmDialog;

@FXMLController
public class RestaurantControllerImpl implements RestaurantController {

    final private static Logger logger = LoggerFactory.getLogger(RestaurantControllerImpl.class);

    static final int INITIAL_GRID_SIZE = 10;

    @FXML
    private AnchorPane rootRestaurant;

    @FXML
    private Button dailySummary;

    @FXML
    private Button reservation;

    @FXML
    private ToggleButton configuration;

    @FXML
    private ToggleButton motion;

    @FXML
    private AnchorPane tablesTab;

    @FXML
    private Label tablesControl;

    @FXML
    private AnchorPane loiterersTab;

    @FXML
    private Label loiterersControl;

    @FXML
    private AnchorPane frequentersTab;

    @FXML
    private Label frequentersControl;

    @FXML
    private AnchorPane employeesTab;

    @FXML
    private Label employeesControl;

    @FXML
    private Label liveTime;

    @FXML
    private ImageView layoutImage;

    @Value("${layout.image}")
    Resource layoutImageResource;

    private Popup tableForm;

    @Autowired
    DailyClosureService dailyClosureService;

    @Autowired
    private TableConfigurationController tableConfigurationController;

    @Autowired
    private DailySummaryController dailySummaryController;

    @Getter
    private TableType tableType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initContextMenu(tablesControl);
        initContextMenu(loiterersControl);
        initContextMenu(frequentersControl);
        initContextMenu(employeesControl);
        tableConfigurationController.initialize();
        initLayoutImage();
        initLiveTime(liveTime);
    }

    private void initLayoutImage() {
        try {
            Image layoutImagePicture = new Image(layoutImageResource.getInputStream());
        layoutImage.setImage(layoutImagePicture);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initContextMenu(Label control) {
        addPressAndHold(control,
                new RestaurantContextMenuBuilderDecorator(
                        tableConfigurationController,
                        this,
                        new BaseContextMenuBuilder())
        );
    }

    @FXML
    public void onConfigurationToggle(Event event) {
        logger.info("The configuration mode was toggled: is config mode: " + configuration.isSelected());
        if (!configuration.isSelected()) {
            tableConfigurationController.clearSelections();
        }
    }

    @FXML
    public void onDailySummary(Event event) {
        WaiterApp.showView(DailySummaryFxmlView.class);
        dailySummaryController.enter();
        logger.info("Entering the Daily Summary.");
    }

    @FXML
    public void onReservationClicked(Event event) {
        WaiterApp.showView(ReservationFxmlView.class);
        logger.info("Entering the Reservations.");
    }

    @FXML
    public void onTablesSelected(Event event) {
        tableType = TableType.NORMAL;
    }

    @FXML
    public void onLoiterersSelected(Event event) {
        tableType = TableType.LOITERER;
    }

    @FXML
    public void onFrequentersSelected(Event event) {
        tableType = TableType.FREQUENTER;
    }

    @FXML
    public void onEmployeesSelected(Event event) {
        tableType = TableType.EMPLOYEE;
    }

    @Override
    public void addNodeToPane(Node node, TableType tableType) {
        switch (tableType) {
            case NORMAL:
                moveNode(node, tablesTab);
                break;
            case LOITERER:
                moveNode(node, loiterersTab);
                break;
            case FREQUENTER:
                moveNode(node, frequentersTab);
                break;
            case EMPLOYEE:
                moveNode(node, employeesTab);
                break;
        }
    }

    @Override
    public Pane getActiveTab() {
        switch (tableType) {
            case NORMAL:
                return tablesTab;
            case LOITERER:
                return loiterersTab;
            case FREQUENTER:
                return frequentersTab;
            case EMPLOYEE:
                return employeesTab;
            default:
                return tablesTab;
        }
    }

    @Override
    public Node getRootNode() {
        return rootRestaurant;
    }

    @Override
    public boolean isConfigurationMode() {
        return configuration.isSelected();
    }

    @Override
    public boolean isMotionMode() {
        return motion.isSelected();
    }
}
