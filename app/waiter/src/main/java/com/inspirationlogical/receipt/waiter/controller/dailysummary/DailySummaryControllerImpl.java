package com.inspirationlogical.receipt.waiter.controller.dailysummary;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by TheDagi on 2017. 04. 21..
 */
@Singleton
public class DailySummaryControllerImpl extends AbstractRetailControllerImpl
    implements DailySummaryController {

    public static final String DAILY_SUMMARY_VIEW_PATH = "/view/fxml/DailySummary.fxml";

    @FXML
    private BorderPane root;

    @FXML
    private Label openConsumption;
    @FXML
    private Label cashTotalPrice;
    @FXML
    private Label creditCardTotalPrice;
    @FXML
    private Label couponTotalPrice;
    @FXML
    private Label totalPrice;
    @FXML
    private Button printDailyConsumption;

    @FXML
    private Label liveTime;

    private RestaurantView restaurantView;

    private String openConsumptionString;

    @Inject
    public DailySummaryControllerImpl(RestaurantService restaurantService,
                                      RetailService retailService,
                                      RestaurantController restaurantController,
                                      TableConfigurationController tableConfigurationController) {
        super(restaurantService, retailService, restaurantController, tableConfigurationController);
    }

    @Override
    public String getViewPath() {
        return DAILY_SUMMARY_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initLiveTime(liveTime);
        updatePriceFields();
    }

    @Override
    protected void soldProductsRowClickHandler(SoldProductViewModel row) {

    }

    @Override
    public void onBackToRestaurantView(Event event) {
        viewLoader.loadViewIntoScene(restaurantController);
    }

    @Override
    public void setRestaurantView(RestaurantView restaurantView) {
        this.restaurantView = restaurantView;
    }

    @Override
    public void setOpenConsumption(String openConsumption) {
        openConsumptionString = openConsumption;
    }

    @Override
    public void updatePriceFields() {
        cashTotalPrice.setText(String.valueOf(restaurantView.getConsumptionOfTheDay(receipt -> receipt.getAdaptee().getPaymentMethod().equals(PaymentMethod.CASH))));
        creditCardTotalPrice.setText(String.valueOf(restaurantView.getConsumptionOfTheDay(receipt -> receipt.getAdaptee().getPaymentMethod().equals(PaymentMethod.CREDIT_CARD))));
        couponTotalPrice.setText(String.valueOf(restaurantView.getConsumptionOfTheDay(receipt -> receipt.getAdaptee().getPaymentMethod().equals(PaymentMethod.COUPON))));
        openConsumption.setText(openConsumptionString);
        int totalPriceInt  = Integer.valueOf(openConsumption.getText()) +
                + Integer.valueOf(cashTotalPrice.getText())
                + Integer.valueOf(creditCardTotalPrice.getText())
                + Integer.valueOf(couponTotalPrice.getText());
        totalPrice.setText(String.valueOf(totalPriceInt));
    }

    @FXML
    public void onPrintDailyConsumption(Event event) {
        retailService.printDailyConsumption(restaurantView);
    }
}
