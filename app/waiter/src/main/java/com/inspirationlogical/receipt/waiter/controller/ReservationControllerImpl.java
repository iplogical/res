package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.entity.Reservation;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.viewmodel.ReservationViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import jfxtras.scene.control.CalendarPicker;
import jfxtras.scene.control.CalendarTimePicker;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.toList;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
@Singleton
public class ReservationControllerImpl extends AbstractController
        implements ReservationController {

    public static final String RESERVATION_VIEW_PATH = "/view/fxml/Reservation.fxml ";

    @FXML
    private BorderPane root;

    @FXML
    private TableView<ReservationViewModel> reservationsTable;
    @FXML
    private TableColumn<ReservationViewModel, String> reservationName;
    @FXML
    private TableColumn<ReservationViewModel, String> reservationTableNumber;
    @FXML
    private TableColumn<ReservationViewModel, String> reservationDate;
    @FXML
    private TableColumn<ReservationViewModel, String> reservationStartTime;
    @FXML
    private TableColumn<ReservationViewModel, String> reservationEndTime;

    @FXML
    private TextField name;
    @FXML
    private TextField tableNumber;
    @FXML
    private TextField questCount;
    @FXML
    private TextArea note;

    @FXML
    private HBox dateContainer;
    @FXML
    private HBox startTimeContainer;
    @FXML
    private HBox endTimeContainer;

    private CalendarPicker date;

    private CalendarTimePicker startTime;
    private CalendarTimePicker endTime;

    @Inject
    private ViewLoader viewLoader;

    private RestaurantService restaurantService;

    private RetailService retailService;

    private RestaurantController restaurantController;

    private RestaurantView restaurantView;

    private List<ReservationView> reservationViews;

    private ObservableList<ReservationViewModel> reservationModels;

    @Inject
    public ReservationControllerImpl(RestaurantService restaurantService,
                                     RetailService retailService,
                                     RestaurantController restaurantController) {
        this.restaurantService = restaurantService;
        this.retailService = retailService;
        this.restaurantController = restaurantController;
        this.date = new CalendarPicker();
        this.startTime = new CalendarTimePicker();
        this.endTime = new CalendarTimePicker();
    }

    @Override
    public String getViewPath() {
        return RESERVATION_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDate();
        initStartTime();
        initEndTime();
        initColumns();
        initReservations();
    }

    @Override
    public void setRestaurantView(RestaurantView restaurantView) {
        this.restaurantView = restaurantView;
    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        viewLoader.loadViewIntoScene(restaurantController);
    }

    private void initDate() {
        date.setCalendar(Calendar.getInstance());
        date.setLocale(Locale.forLanguageTag("hu-HU"));
        date.setPrefWidth(400);
        dateContainer.getChildren().add(date);
    }

    private void initStartTime() {
        startTime.setCalendar(Calendar.getInstance());
        startTime.setLocale(Locale.forLanguageTag("hu-HU"));
        startTime.setPrefWidth(400);
        startTime.setMinuteStep(15);
        startTimeContainer.getChildren().add(startTime);
    }

    private void initEndTime() {
        endTime.setCalendar(Calendar.getInstance());
        endTime.setLocale(Locale.forLanguageTag("hu-HU"));
        endTime.setPrefWidth(400);
        endTime.setMinuteStep(15);
        endTimeContainer.getChildren().add(endTime);
    }

    private void initColumns() {
        initColumn(reservationName, ReservationViewModel::getName);
        initColumn(reservationTableNumber, ReservationViewModel::getTableNumber);
        initColumn(reservationDate, ReservationViewModel::getDate);
        initColumn(reservationStartTime, ReservationViewModel::getStartTime);
        initColumn(reservationEndTime, ReservationViewModel::getEndTime);
    }

    private void initReservations() {
        reservationViews = restaurantService.getReservations(LocalDate.now());
        List<ReservationViewModel> models = reservationViews.stream().map(ReservationViewModel::new).collect(toList());
        reservationModels = FXCollections.observableArrayList(models);
        reservationsTable.setItems(reservationModels);
    }
}
