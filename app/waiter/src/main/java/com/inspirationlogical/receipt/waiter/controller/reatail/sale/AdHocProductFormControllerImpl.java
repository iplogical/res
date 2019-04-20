package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.params.AdHocProductParams;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addFormDragAndDrop;

@FXMLController
public class AdHocProductFormControllerImpl implements AdHocProductFormController {
    private static final Logger logger = LoggerFactory.getLogger(WaiterApp.class);

    @FXML
    private VBox root;

    @FXML
    private TextField adHocProductName;

    @FXML
    private TextField adHocProductQuantity;

    @FXML
    private TextField adHocProductSalePrice;

    @FXML
    private TextField adHocProductPurchasePrice;

    @Autowired
    private SaleController saleController;

    private AdHocProductParams adHocProductParams;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addFormDragAndDrop(root);
        adHocProductQuantity.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        adHocProductSalePrice.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        if(adHocProductName.getText().isEmpty()) return;
        try {
            adHocProductParams = AdHocProductParams.builder()
                    .name(adHocProductName.getText())
                    .quantity(Integer.valueOf(adHocProductQuantity.getText()))
                    .purchasePrice(Integer.valueOf(adHocProductPurchasePrice.getText()))
                    .salePrice(Integer.valueOf(adHocProductSalePrice.getText()))
                    .build();
            saleController.sellAdHocProduct(adHocProductParams);
        } catch (NumberFormatException e) {
            logger.error("Wrong adHocParams.", e);
            ErrorMessage.showErrorMessage(saleController.getRootNode(),
                    WaiterResources.WAITER.getString("AdHocProductForm.NumberFormatError"));
        }
    }

    @FXML
    public void onCancel(MouseEvent event) {
        saleController.hideAdHocProductForm();
    }

    @Override
    public Node getRootNode() {
        return root;
    }

}

