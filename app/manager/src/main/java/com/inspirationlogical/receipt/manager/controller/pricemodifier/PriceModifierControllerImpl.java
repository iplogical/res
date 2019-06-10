package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.corelib.utility.NotificationMessage;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsFxmlView;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import com.inspirationlogical.receipt.manager.viewmodel.PriceModifierViewModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
@FXMLController
public class PriceModifierControllerImpl implements PriceModifierController {

    @FXML
    private BorderPane root;

    @FXML
    private TableView<PriceModifierViewModel> priceModifierTable;
    @FXML
    private TableColumn<PriceModifierViewModel, String> ownerLongName;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierName;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierType;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierQuantityLimit;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierDiscountPercent;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierStartDate;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierEndDate;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierRepeatPeriod;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierPeriodMultiplier;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierStartTime;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierEndTime;
    @FXML
    private TableColumn<PriceModifierViewModel, String> priceModifierDayOfWeek;

    @FXML
    Button createItem;
    @FXML
    Button modifyItem;
    @FXML
    Button deleteItem;
    @FXML
    Button showGoods;


    @Autowired
    private GoodsController goodsController;

    @Autowired
    private PriceModifierFormController priceModifierFormController;

    @Autowired
    private ManagerService managerService;

    private Popup priceModifierForm;

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
        updatePriceModifiers();
    }

    @Override
    public void addPriceModifier(PriceModifierParams params) {
        if(params.getOriginalName().equals("")) {
            managerService.addPriceModifier(params);
        } else {
            managerService.updatePriceModifier(params);
        }
        updatePriceModifiers();
        priceModifierForm.hide();
    }


    @FXML
    public void onShowGoods(Event event) {
        ManagerApp.showView(GoodsFxmlView.class);
    }

    @FXML
    public void onCreateItem(Event event) {
        showPriceModifierForm();
        priceModifierFormController.clearPriceModifierForm();
    }

    private void showPriceModifierForm() {
        priceModifierForm = new Popup();
        priceModifierForm.getContent().add(ManagerApp.getRootNode(PriceModifierFormFxmlView.class));
        priceModifierFormController.loadPriceModifierForm(this);
        showPopup(priceModifierForm, priceModifierFormController, root, new Point2D(520, 200));
    }

    @Override
    public void hidePriceModifierForm() {
        priceModifierForm.hide();
    }

    @FXML
    public void onModifyItem(Event event) {
        if(isSelectionNull()) {
            NotificationMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("PriceModifier.SelectPriceModifierForModify"));
            return;
        }
        showPriceModifierForm();
        priceModifierFormController.loadPriceModifierForm(getSelectedPriceModifier());
    }

    private boolean isSelectionNull() {
        return getSelectedPriceModifier() == null;
    }

    private PriceModifierViewModel getSelectedPriceModifier() {
        return priceModifierTable.getSelectionModel().getSelectedItem();
    }

    @FXML
    public void onDeleteItem(Event event) {
        if(isSelectionNull()) {
            NotificationMessage.showErrorMessage(root,
                    ManagerResources.MANAGER.getString("PriceModifier.SelectPriceModifierForDelete"));
            return;
        }
        PriceModifierViewModel priceModifier = getSelectedPriceModifier();
        PriceModifierParams params = PriceModifierParams.builder()
                .originalName(priceModifier.getName())
                .build();
        managerService.deletePriceModifier(params);
        updatePriceModifiers();
    }

    private PriceModifierViewModel getViewModel(TableColumn.CellDataFeatures<PriceModifierViewModel, String> cellDataFeatures) {
        return cellDataFeatures.getValue();
    }

    private void initColumns() {
        initColumn(priceModifierName, PriceModifierViewModel::getName);
        initColumn(ownerLongName, PriceModifierViewModel::getOwnerName);
        initColumn(priceModifierType, PriceModifierViewModel::getType);
        initColumn(priceModifierQuantityLimit, PriceModifierViewModel::getQuantityLimit);
        initColumn(priceModifierDiscountPercent, PriceModifierViewModel::getDiscountPercent);
        initColumn(priceModifierStartDate, PriceModifierViewModel::getStartDate);
        initColumn(priceModifierEndDate, PriceModifierViewModel::getEndDate);
        initColumn(priceModifierRepeatPeriod, PriceModifierViewModel::getRepeatPeriod);
        initColumn(priceModifierPeriodMultiplier, PriceModifierViewModel::getPeriodMultiplier);
        initColumn(priceModifierStartTime, PriceModifierViewModel::getStartTime);
        initColumn(priceModifierEndTime, PriceModifierViewModel::getEndTime);
        initColumn(priceModifierDayOfWeek, PriceModifierViewModel::getDayOfWeek);
    }

    private void initColumn(TableColumn<PriceModifierViewModel, String> tableColumn, Function<PriceModifierViewModel, String> method) {
        tableColumn.setCellValueFactory((TableColumn.CellDataFeatures<PriceModifierViewModel, String> category) ->
                new ReadOnlyStringWrapper(method.apply(getViewModel(category))));
    }

    private void updatePriceModifiers() {
        priceModifierTable.getItems().clear();
        managerService.getPriceModifiers().forEach(priceModifierView -> priceModifierTable.getItems().add(new PriceModifierViewModel(priceModifierView)));
    }
}
