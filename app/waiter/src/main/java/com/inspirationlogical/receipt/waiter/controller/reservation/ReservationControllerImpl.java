package com.inspirationlogical.receipt.waiter.controller.reservation;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.reservation.ReservationService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import de.felixroske.jfxsupport.FXMLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import jfxtras.scene.control.CalendarTimePicker;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
@FXMLController
public class ReservationControllerImpl extends AbstractController
        implements ReservationController {

    private static final String RESERVATION_VIEW_PATH = "/view/fxml/Reservation.fxml";

    @FXML
    private BorderPane root;

    @FXML
    private TableView<ReservationViewModel> reservationsTable;
    @FXML
    private TableColumn<ReservationViewModel, String> reservationName;
    @FXML
    private TableColumn<ReservationViewModel, String> reservationTableNumber;
    @FXML
    private TableColumn<ReservationViewModel, String> reservationGuestCount;
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
    private TextArea reservationNote;

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

    private CalendarPickerWrapper datePickerWrapper;

    private CalendarTimePicker startTime;
    private CalendarTimePicker endTime;

    private RestaurantService restaurantService;

    private ReservationService reservationService;

    private RestaurantController restaurantController;

    private TableConfigurationController tableConfigurationController;

    private RestaurantView restaurantView;

    private List<ReservationView> reservationViews;

    private ObservableList<ReservationViewModel> reservationModels;

    private long reservationId;

    @Autowired
    public ReservationControllerImpl(RestaurantService restaurantService,
                                     ReservationService reservationService,
                                     RestaurantController restaurantController,
                                     TableConfigurationController tableConfigurationController) {
        this.restaurantService = restaurantService;
        this.reservationService = reservationService;
        this.restaurantController = restaurantController;
        this.tableConfigurationController = tableConfigurationController;
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
        initDatePicker();
        initStartTime();
        initEndTime();
        initColumns();
        initReservations();
        initializeReservationsTableRowHandler();
        initLiveTime(liveTime);
    }

    private void initDatePicker() {
        datePickerWrapper = CalendarPickerWrapper.getInstance();
        dateContainer.getChildren().add(datePickerWrapper.getDate());
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
        initColumn(reservationGuestCount, ReservationViewModel::getGuestCount);
        initColumn(reservationStartTime, ReservationViewModel::getStartTime);
        initColumn(reservationEndTime, ReservationViewModel::getEndTime);
    }

    private void initReservations() {
        reservationViews = reservationService.getReservations(datePickerWrapper.getSelectedDate());
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
        reservationNote.setText(row.getNote());
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

    private void clearForm() {
        name.clear();
        tableNumber.clear();
        guestCount.clear();
        phoneNumber.clear();
        reservationNote.clear();
        startTime.setCalendar(Calendar.getInstance());
        endTime.setCalendar(Calendar.getInstance());
    }

    @Override
    public void setRestaurantView(RestaurantView restaurantView) {
        this.restaurantView = restaurantView;
    }

    @FXML
    public void onBackToRestaurantView(Event event) {
        WaiterApp.showView(RestaurantFxmlView.class);
    }

    @FXML
    public void onConfirm(Event event) {
        try {
            reservationService.addReservation(initReservationParams());
            initReservations();
            clearForm();
        } catch (NumberFormatException e) {
            showNumberFormatErrorMessage();
        }
    }

    private void showNumberFormatErrorMessage() {
        ErrorMessage.showErrorMessage(getRootNode(), WaiterResources.WAITER.getString("Reservation.NumberFormatError"));
    }

    private ReservationParams initReservationParams() {
        int startHour = getLocalTimeWithZoneId(startTime).getHour();
        int startMinute = getLocalTimeWithZoneId(startTime).getMinute();
        int endHour = getLocalTimeWithZoneId(endTime).getHour();
        int endMinute = getLocalTimeWithZoneId(endTime).getMinute();
        return buildReservationParams(startHour, startMinute, endHour, endMinute);
    }

    private ReservationParams buildReservationParams(int startHour, int startMinute, int endHour, int endMinute) throws NumberFormatException{
        return ReservationParams.builder()
                .name(name.getText())
                .note(reservationNote.getText())
                .tableNumber(Integer.valueOf(tableNumber.getText()))
                .guestCount(Integer.valueOf(guestCount.getText()))
                .phoneNumber(phoneNumber.getText())
                .date(LocalDateTime.ofInstant(datePickerWrapper.getDate().getCalendar().getTime().toInstant(), ZoneId.systemDefault()).toLocalDate())
                .startTime(LocalTime.of(startHour, startMinute))
                .endTime(LocalTime.of(endHour, endMinute))
                .build();
    }

    private LocalTime getLocalTimeWithZoneId(CalendarTimePicker picker) {
        return LocalDateTime.ofInstant(picker.getCalendar().getTime().toInstant(), ZoneId.systemDefault()).toLocalTime();
    }

    @FXML
    public void onUpdate(Event event) {
        try {
            List<ReservationView> selectedReservations = getSelectedReservation();
            if(selectedReservations.size() == 0) return;
            reservationService.updateReservation(selectedReservations.get(0).getId(), initReservationParams());
            initReservations();
            clearForm();
        } catch (NumberFormatException e) {
            showNumberFormatErrorMessage();
        }
    }

    private List<ReservationView> getSelectedReservation() {
        return reservationViews.stream()
                .filter(reservationView -> reservationView.getId() == reservationId)
                .collect(toList());
    }

    @FXML
    public void onDelete(Event event) {
        List<ReservationView> selectedReservations = getSelectedReservation();
        if(selectedReservations.size() == 0) return;
        reservationService.deleteReservation(selectedReservations.get(0).getId());
        initReservations();
        clearForm();
    }

    @FXML
    public void onOpenTable(Event event) {
        try{
            List<ReservationView> selectedReservations = getSelectedReservation();
            if(selectedReservations.size() == 0) return;
            tableConfigurationController.openTableOfReservation(selectedReservations.get(0));
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }
}
