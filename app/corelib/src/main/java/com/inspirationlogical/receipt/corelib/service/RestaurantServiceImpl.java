package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.exception.RestaurantNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
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

import java.util.List;

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
    public List<TableView> exchangeTables(int selectedTableNumber, int otherTableNumber) {
        return tableServiceConfig.exchangeTables(selectedTableNumber, otherTableNumber);
    }

    @Override
    public TableView updateTableParams(int number, TableParams tableParams) {
        return tableServiceConfig.updateTableParams(number, tableParams);
    }

    @Override
    public TableView setGuestCount(int tableNumber, int guestCount) {
        return tableServiceConfig.setGuestCount(tableNumber, guestCount);
    }

    @Override
    public TableView setTablePosition(int tableNumber, Point2D position) {
        return tableServiceConfig.setPosition(tableNumber, position);
    }

    @Override
    public TableView rotateTable(int tableNumber) {
        return tableServiceConfig.rotateTable(tableNumber);
    }

    @Override
    public void closeDay() {
        stockService.closeLatestStockEntries();
        dailyClosureService.close();
    }
}
