package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.adapter.DailyClosureAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ReservationAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.ReservationParams;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.repository.DailyClosureRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.RestaurantRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.service.sub.TableService;
import javafx.geometry.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.inspirationlogical.receipt.corelib.model.enums.TableType.canBeHosted;
import static java.util.stream.Collectors.toList;

@Service
@Transactional
public class RestaurantServiceImpl extends AbstractService implements RestaurantService {

    final private static Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private DailyClosureRepository dailyClosureRepository;

    @Autowired
    private TableService tableService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    RestaurantServiceImpl(EntityViews entityViews) {
        super(entityViews);
    }

    @Override
    public RestaurantView getActiveRestaurant() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        if (restaurants.isEmpty()) {
            throw new RestaurantNotFoundException();
        }
        return new RestaurantViewImpl(restaurants.get(0), getConsumptionOfTheDay());
    }

    private Map<PaymentMethod, Integer> getConsumptionOfTheDay() {
        Map<PaymentMethod, Integer> consumptionOfTheDay = new HashMap<>();
        consumptionOfTheDay.put(PaymentMethod.CASH, getConsumptionOfTheDay(PaymentMethod.CASH));
        consumptionOfTheDay.put(PaymentMethod.CREDIT_CARD, getConsumptionOfTheDay(PaymentMethod.CREDIT_CARD));
        consumptionOfTheDay.put(PaymentMethod.COUPON, getConsumptionOfTheDay(PaymentMethod.COUPON));
        consumptionOfTheDay.put(null, getConsumptionOfTheDay(null));
        return consumptionOfTheDay;
    }

    private int getConsumptionOfTheDay(PaymentMethod paymentMethod) {
        DailyClosure openDailyClosure = dailyClosureRepository.findByClosureTimeIsNull();
        if(paymentMethod == null)
            return openDailyClosure.getSumSaleGrossPriceCash() + openDailyClosure.getSumSaleGrossPriceCreditCard();
        if(paymentMethod.equals(PaymentMethod.CASH))
            return openDailyClosure.getSumSaleGrossPriceCash();
        if(paymentMethod.equals(PaymentMethod.CREDIT_CARD))
            return openDailyClosure.getSumSaleGrossPriceCreditCard();
        if(paymentMethod.equals(PaymentMethod.COUPON))
            return openDailyClosure.getSumSaleGrossPriceCoupon();
        else
            return 0;
    }

    @Override
    public ReceiptView getOpenReceipt(TableView tableView) {
        Receipt receipt = receiptRepository.getOpenReceipt(ReceiptStatus.OPEN, tableView.getNumber());
        receipt.getRecords().size();
        return new ReceiptViewImpl(new ReceiptAdapterBase(receipt));
    }

    @Override
    public List<TableView> getTables() {
        return createViewsFromAdapters(TableAdapter.getDisplayableTables(), TableViewImpl::new);
    }

    @Override
    public int getFirstUnusedNumber() {
        return tableRepository.getFirstUnusedNumber();
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableParams tableParams) {
        logger.info("A table was added: " + tableParams);
        return new TableViewImpl(tableService.addTable(restaurant, tableParams));

    }

    @Override
    public void deleteTable(TableView tableView) {
        getTableAdapter(tableView).deleteTable();
    }

    @Override
    public void mergeTables(TableView consumer, List<TableView> consumed) {
        TableAdapter consumerTableAdapter = getTableAdapter(consumer);
        List<TableAdapter> consumedTableAdapters = consumed.stream()
                .map(this::getTableAdapter)
                .collect(toList());
//        getRestaurantAdapter(getActiveRestaurant()).mergeTables(consumerTableAdapter, consumedTableAdapters);
        tableService.mergeTables(consumerTableAdapter, consumedTableAdapters);
    }

    @Override
    public List<TableView> splitTables(TableView consumer) {
        TableAdapter consumerTableAdapter = getTableAdapter(consumer);
        return tableService.splitTables(consumerTableAdapter)
                .stream()
                .map(TableViewImpl::new)
                .collect(toList());
    }

    @Override
    public void exchangeTables(List<TableView> tables) {
        TableAdapter firstTable = getTableAdapter(tables.get(0));
        firstTable.exchangeTables(getTableAdapter(tables.get(1)));
    }

    @Override
    public void setTableNumber(TableView tableView, int tableNumber, RestaurantView restaurant) {
        TableAdapter tableAdapter = getTableAdapter(tableView);
        if(tableService.isTableNumberAlreadyInUse(tableNumber)) {
            if(canBeHosted(tableView.getType())) {
                tableAdapter.setHost(tableNumber);
            } else {
                throw new IllegalTableStateException("The table number " + tableView.getNumber() + " is already in use");
            }
            return;
        }
        tableAdapter.removePreviousHost();
        tableAdapter.setNumber(tableNumber);
    }

    @Override
    public void setTableParams(TableView tableView, TableParams tableParams) {
        getTableAdapter(tableView).setTableParams(tableParams);
    }

    @Override
    public void setGuestCount(TableView tableView, int guestCount) {
        getTableAdapter(tableView).setGuestCount(guestCount);
    }

    @Override
    public void setTablePosition(TableView tableView, Point2D position) {
        getTableAdapter(tableView).setPosition(position);
    }

    @Override
    public void rotateTable(TableView tableView) {
        getTableAdapter(tableView).rotateTable();
    }

    @Override
    public void closeDay() {
        StockAdapter.closeLatestStockEntries();
//        ReceiptAdapterBase.deleteReceipts();
        DailyClosureAdapter.getOpenDailyClosure().close();
    }

    @Override
    public List<ReservationView> getReservations() {
        return ReservationAdapter.getReservations()
                .stream()
                .map(ReservationViewImpl::new)
                .collect(toList());
    }

    @Override
    public List<ReservationView> getReservations(LocalDate date) {
        return ReservationAdapter.getReservationsByDate(date)
                .stream()
                .map(ReservationViewImpl::new)
                .collect(toList());
    }

    @Override
    public long addReservation(ReservationParams params) {
        return ReservationAdapter.addReservation(params);
    }

    @Override
    public void updateReservation(ReservationView selectedReservation, ReservationParams reservationParams) {
        getReservationAdapter(selectedReservation).updateReservation(reservationParams);
    }

    @Override
    public void updateReservation(long reservationId, ReservationParams reservationParams) {
        ReservationAdapter.getReservationById(reservationId).updateReservation(reservationParams);
    }

    @Override
    public void deleteReservation(long reservationId) {
        ReservationAdapter.getReservationById(reservationId).deleteReservation();
    }
}
