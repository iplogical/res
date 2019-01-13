package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.repository.DailyClosureRepository;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.RestaurantRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import com.inspirationlogical.receipt.corelib.service.daily_closure.DailyClosureService;
import com.inspirationlogical.receipt.corelib.service.stock.StockService;
import com.inspirationlogical.receipt.corelib.service.table.TableServiceConfig;
import javafx.geometry.Point2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private DailyClosureService dailyClosureService;

    @Autowired
    private TableServiceConfig tableServiceConfig;

    @Autowired
    private StockService stockService;

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
        return new RestaurantView(restaurants.get(0));
    }

    @Override
    public ReceiptView getOpenReceipt(TableView tableView) {
        Receipt receipt = receiptRepository.getOpenReceipt(tableView.getNumber());
        if (receipt == null) {
            return null;
        }
        return new ReceiptView(receipt);
    }

    @Override
    public List<TableView> getDisplayableTables() {
        return tableServiceConfig.getDisplayableTables();
    }

    @Override
    public int getFirstUnusedNumber() {
        return tableRepository.getFirstUnusedNumber();
    }

    @Override
    public TableView addTable(RestaurantView restaurant, TableParams tableParams) {
        logger.info("A table was added: " + tableParams);
        return tableServiceConfig.addTable(restaurant, tableParams);
    }

    @Override
    public void deleteTable(TableView tableView) {
        tableServiceConfig.deleteTable(tableView);
    }

    @Override
    public void exchangeTables(TableView selected, TableView other) {
        tableServiceConfig.exchangeTables(selected, other);
    }

    @Override
    public void setTableNumber(TableView tableView, int tableNumber) {
        tableServiceConfig.setTableNumber(tableView, tableNumber);
    }

    @Override
    public void setTableParams(TableView tableView, TableParams tableParams) {
        tableServiceConfig.setTableParams(tableView, tableParams);
    }

    @Override
    public void setGuestCount(TableView tableView, int guestCount) {
        tableServiceConfig.setGuestCount(tableView, guestCount);
    }

    @Override
    public void setTablePosition(TableView tableView, Point2D position) {
        tableServiceConfig.setPosition(tableView, position);
    }

    @Override
    public void rotateTable(TableView tableView) {
        tableServiceConfig.rotateTable(tableView);
    }

    @Override
    public void closeDay() {
        stockService.closeLatestStockEntries();
        dailyClosureService.close();
    }
}
