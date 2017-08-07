package com.inspirationlogical.receipt.waiter.controller.reservation;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;

import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import jfxtras.scene.control.CalendarPicker;
import jfxtras.scene.control.CalendarTimePicker;
import lombok.SneakyThrows;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
@Singleton
public class ReservationControllerImpl extends AbstractController
        implements ReservationController {

    public static final String RESERVATION_VIEW_PATH = "/view/fxml/Reservation.fxml";

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
    private TextField guestCount;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextArea note;

    @FXML
    private HBox dateContainer;
    @FXML
    private HBox startTimeContainer;
    @FXML
    private HBox endTimeContainer;

    @FXML
    private Button confirm;
    @FXML
    private Button update;
    @FXML
    private Button delete;
    @FXML
    private Button openTable;

    @FXML
    private Label liveTime;

    private CalendarPicker date;
    private LocalDate selectedDate;

    private CalendarTimePicker startTime;
    private CalendarTimePicker endTime;

    @Inject
    private ViewLoader viewLoader;

    private RestaurantService restaurantService;

    private RetailService retailService;

    private RestaurantController restaurantController;

    private TableConfigurationController tableConfigurationController;

    private RestaurantView restaurantView;

    private List<ReservationView> reservationViews;

    private ObservableList<ReservationViewModel> reservationModels;

    private long reservationId;

    @Inject
    public ReservationControllerImpl(RestaurantService restaurantService,
                                     RetailService retailService,
                                     RestaurantController restaurantController,
                                     TableConfigurationController tableConfigurationController) {
        this.restaurantService = restaurantService;
        this.retailService = retailService;
        this.restaurantController = restaurantController;
        this.tableConfigurationController = tableConfigurationController;
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
        initializeReservationsTableRowHandler();
        initLiveTime(liveTime);
    }

    private void initDate() {
        date.setCalendar(Calendar.getInstance());
        selectedDate = LocalDate.now();
        date.setLocale(Locale.forLanguageTag("hu-HU"));
        date.setPrefWidth(400);
        dateContainer.getChildren().add(date);
        date.calendarProperty().addListener((observable, oldValue, newValue) -> {
            selectedDate = LocalDateTime.ofInstant(newValue.getTime().toInstant(), ZoneId.systemDefault()).toLocalDate();
            initReservations();
        });
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
        reservationViews = restaurantService.getReservations(selectedDate);
        List<ReservationViewModel> models = reservationViews.stream().map(ReservationViewModel::new).collect(toList());
        models.sort(Comparator.comparing(ReservationViewModel::getTableNumberAsInt));
        reservationModels = FXCollections.observableArrayList(models);
        reservationsTable.setItems(reservationModels);
    }

    private void initializeReservationsTableRowHandler() {
        reservationsTable.setRowFactory(tv -> {
            TableRow<ReservationViewModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 1 && (! row.isEmpty())) {
                    rowClickHandler(row.getItem());
                } else if(row.isEmpty()) {
                    clearForm();
                }
            });
            return row;
        });
    }

    @SneakyThrows
    private void rowClickHandler(ReservationViewModel row) {
        name.setText(row.getName());
        tableNumber.setText(row.getTableNumber());
        guestCount.setText(row.getGuestCount());
        phoneNumber.setText(row.getPhoneNumber());
        note.setText(row.getNote());
        setTimes(row);
        reservationId = row.getReservationId();
    }

    private void setTimes(ReservationViewModel row) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        calendar.setTime(dateFormat.parse(row.getStartTime()));
        startTime.setCalendar(calendar);
        calendar.setTime(dateFormat.parse(row.getEndTime()));
        endTime.setCalendar(calendar);
    }

    @Override
    public void setRestaurantView(RestaurantView restaurantView) {
        this.restaurantView = restaurantView;
    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        viewLoader.loadViewIntoScene(restaurantController);
    }

    @FXML
    public void onConfirm(Event event) {
        try {
            restaurantService.addReservation(initReservationParams());
            initReservations();
            clearForm();
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onUpdate(Event event) {
        try {
            List<ReservationView> selectedReservations = getSelectedReservation();
            if(selectedReservations.size() == 0) return;
            restaurantService.updateReservation(selectedReservations.get(0), initReservationParams());
            initReservations();
            clearForm();
        } catch (NumberFormatException e) {

        }
    }

    public List<ReservationView> getSelectedReservation() {
        return reservationViews.stream()
                .filter(reservationView -> reservationView.getId() == reservationId)
                .collect(toList());
    }

    @FXML
    public void onDelete(Event event) {
        List<ReservationView> selectedReservations = getSelectedReservation();
        if(selectedReservations.size() == 0) return;
        restaurantService.deleteReservation(selectedReservations.get(0).getId());
        initReservations();
        clearForm();
    }

    @FXML
    public void onOpenTable(Event event) {
        try{
            tableConfigurationController.openTableOfReservation(Integer.valueOf(tableNumber.getText()), name.getText(), Integer.valueOf(guestCount.getText()), note.getText());
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    private ReservationParams initReservationParams() {
        int startHour = getLocalTimeWithZoneId(startTime).getHour();
        int startMinute = getLocalTimeWithZoneId(startTime).getMinute();
        int endHour = getLocalTimeWithZoneId(endTime).getHour();
        int endMinute = getLocalTimeWithZoneId(endTime).getMinute();
        return buildReservationParams(startHour, startMinute, endHour, endMinute);
    }

    private LocalTime getLocalTimeWithZoneId(CalendarTimePicker picker) {
        return LocalDateTime.ofInstant(picker.getCalendar().getTime().toInstant(), ZoneId.systemDefault()).toLocalTime();
    }

    private ReservationParams buildReservationParams(int startHour, int startMinute, int endHour, int endMinute) throws NumberFormatException{
        return ReservationParams.builder()
                .name(name.getText())
                .note(note.getText())
                .tableNumber(Integer.valueOf(tableNumber.getText()))
                .guestCount(Integer.valueOf(guestCount.getText()))
                .phoneNumber(phoneNumber.getText())
                .date(LocalDateTime.ofInstant(date.getCalendar().getTime().toInstant(), ZoneId.systemDefault()).toLocalDate())
                .startTime(LocalTime.of(startHour, startMinute))
                .endTime(LocalTime.of(endHour, endMinute))
                .build();
    }

    private void clearForm() {
        name.clear();
        tableNumber.clear();
        guestCount.clear();
        phoneNumber.clear();
        note.clear();
        startTime.setCalendar(Calendar.getInstance());
        endTime.setCalendar(Calendar.getInstance());
    }
}
