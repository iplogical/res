package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bálint on 2017.03.28..
 */
public class PaymentControllerImpl extends AbstractRetailControllerImpl
        implements PaymentController {

    public static final String PAYMENT_VIEW_PATH = "/view/fxml/PaymentView.fxml";

    @FXML
    private BorderPane root;

    @FXML
    RadioButton paymentMethodCash;
    @FXML
    RadioButton paymentMethodCreditCard;
    @FXML
    RadioButton paymentMethodCoupon;

    @FXML
    ToggleButton selectivePayment;
    @FXML
    ToggleButton partialPayment;
    @FXML
    ToggleButton automaticGameFee;

    @FXML
    Button manualGameFee;

    private PaymentViewState paymentViewState;

    @Inject
    public PaymentControllerImpl(RetailServices retailServices,
                                 RestaurantServices restaurantServices,
                                 RestaurantController restaurantController) {
        super(restaurantServices, retailServices, restaurantController);
        paymentViewState = new PaymentViewState();
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setInitialPaymentMethod();
        updateNode();
        initializeTableSummary();
    }

    @FXML
    public void onPaymentMethodToggleAction(Event event) {
        setPaymentMethod();
    }

    @FXML
    public void onSelectivePaymentToggleAction(Event event) {
        setSelectivePayment();
    }

    @FXML
    public void onPartialPaymentToggleAction(Event event) {
        setPartialPayment();
    }

    @FXML
    public void onAutomaticGameFeeToggleAction(Event event) {
        setAutomaticGameFee();
    }

    @FXML
    public void onPay(Event event) {
        if(!paymentViewState.isSelectivePayment()) {
            PaymentParams paymentParams = PaymentParams.builder()
                    .paymentMethod(paymentViewState.getPaymentMethod())
                    .discountPercent(paymentViewState.getDiscountPercent())
                    .discountAbsolute(paymentViewState.getDiscountAbsolute())
                    .build();
            retailServices.payTable(tableView, paymentParams);
            updateNode();
        }
    }

    private void setInitialPaymentMethod() {
        paymentMethodCash.setSelected(true);
    }

    private void updateNode() {
        initializePaymentViewState();
        updateSoldProductsTable();
    }

    private void initializePaymentViewState() {
        setPaymentMethod();
        setSelectivePayment();
        setPartialPayment();
        setAutomaticGameFee();
    }

    private void setPaymentMethod() {
        paymentViewState.setPaymentMethod(getPaymentMethod());
    }

    private void setSelectivePayment() {
        paymentViewState.setSelectivePayment(selectivePayment.isSelected());
    }

    private void setPartialPayment() {
        paymentViewState.setPartialPayment(partialPayment.isSelected());
    }

    private void setAutomaticGameFee() {
        paymentViewState.setAutomaticGameFee(automaticGameFee.isSelected());
    }

    private PaymentMethod getPaymentMethod() {
        if(paymentMethodCash.isSelected()) {
            return PaymentMethod.CASH;
        } else if(paymentMethodCreditCard.isSelected()) {
            return PaymentMethod.CREDIT_CARD;
        } else if(paymentMethodCoupon.isSelected()) {
            return PaymentMethod.COUPON;
        } else return null;
    }
}
