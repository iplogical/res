package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.manager.controller.goods.GoodsController;
import com.inspirationlogical.receipt.manager.viewmodel.PriceModifierViewModel;
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

import javax.inject.Singleton;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
@Singleton
public class PriceModifierControllerImpl implements PriceModifierController {

    public static final String PRICE_MODIFIER_VIEW_PATH = "/view/fxml/PriceModifier.fxml";

    @FXML
    private BorderPane root;

    @FXML
    TableView<PriceModifierViewModel> priceModifierTable;
    @FXML
    TableColumn<PriceModifierViewModel, String> ownerLongName;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierName;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierType;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierQuantityLimit;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierDiscountPercent;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierStartDate;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierEndDate;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierRepeatPeriod;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierPeriodMultiplier;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierStartTime;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierEndTime;
    @FXML
    TableColumn<PriceModifierViewModel, String> priceModifierDayOfWeek;

    @FXML
    Button createItem;
    @FXML
    Button modifyItem;
    @FXML
    Button deleteItem;
    @FXML
    Button showGoods;

    @Inject
    private ViewLoader viewLoader;

    @Inject
    private GoodsController goodsController;

    @Inject
    private CommonService commonService;

    @Inject
    private PriceModifierFormController priceModifierFormController;

    @Inject
    private ManagerService managerService;

    private Popup priceModifierForm;

    @Override
    public String getViewPath() {
        return PRICE_MODIFIER_VIEW_PATH;
    }

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
        managerService.addPriceModifier(params);
        updatePriceModifiers();
        priceModifierForm.hide();
    }

    @FXML
    public void onShowGoods(Event event) {
        viewLoader.loadViewIntoScene(goodsController);
    }

    @FXML
    public void onCreateItem(Event event) {
        priceModifierForm = new Popup();
        priceModifierForm.getContent().add(viewLoader.loadView(priceModifierFormController));
        priceModifierFormController.loadPriceModifierForm(this);
        showPopup(priceModifierForm, priceModifierFormController, root, new Point2D(520, 200));
    }

    @FXML
    public void onModifyItem(Event event) {
    }

    @FXML
    public void onDeleteItem(Event event) {
    }

    private PriceModifierViewModel getViewModel(TableColumn.CellDataFeatures<PriceModifierViewModel, String> cellDataFeatures) {
        return cellDataFeatures.getValue();
    }

    private void initColumn(TableColumn<PriceModifierViewModel, String> tableColumn, Function<PriceModifierViewModel, String> method) {
        tableColumn.setCellValueFactory((TableColumn.CellDataFeatures<PriceModifierViewModel, String> category) ->
                new ReadOnlyStringWrapper(method.apply(getViewModel(category))));
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

    private void updatePriceModifiers() {
        priceModifierTable.getItems().clear();
        managerService.getPriceModifiers().forEach(priceModifierView -> priceModifierTable.getItems().add(new PriceModifierViewModel(priceModifierView)));
    }
}
