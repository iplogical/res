package com.inspirationlogical.receipt.manager.controller;

import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierRepeatPeriod;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierType;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
@Singleton
public class PriceModifierFormControllerImpl implements PriceModifierFormController {

    @FXML
    VBox root;
    @FXML
    TextField name;
    @FXML
    ChoiceBox<String> owner;
    @FXML
    CheckBox isCategory;
    @FXML
    ChoiceBox<PriceModifierType> type;
    @FXML
    TextField quantityMultiplier;
    @FXML
    TextField discountPercent;
    @FXML
    DatePicker startDate;
    @FXML
    DatePicker endDate;
    @FXML
    ChoiceBox<PriceModifierRepeatPeriod> repeatPeriod;
    @FXML
    TextField repeatPeriodMultiplier;
    @FXML
    Button confirm;
    @FXML
    Button cancel;

    private PriceModifierController priceModifierController;

    public static String PRICE_MODIFIER_FORM_VIEW_PATH =  "/view/fxml/PriceModifierForm.fxml";

    @Override
    public String getViewPath() {
        return PRICE_MODIFIER_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO: Move DragAndDropHandler to corelib.
        //addDragAndDrop(root);
    }

    @Override
    public void loadPriceModifierForm(PriceModifierController priceModifierController) {
        this.priceModifierController = priceModifierController;
    }

    @FXML
    public void onConfirm(Event event) {
    }

    @FXML
    public void onCancel(Event event) {
        hideNode(root);
    }
}
