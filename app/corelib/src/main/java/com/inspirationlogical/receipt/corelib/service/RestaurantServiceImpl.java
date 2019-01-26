package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.entity.Receipt;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
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

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private DailyClosureService dailyClosureService;

    @Autowired
    private StockService stockService;

    @Autowired
    RestaurantServiceImpl(EntityViews entityViews) {
        super(entityViews);
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
    public void closeDay() {
        stockService.closeLatestStockEntries();
        dailyClosureService.close();
    }
}
