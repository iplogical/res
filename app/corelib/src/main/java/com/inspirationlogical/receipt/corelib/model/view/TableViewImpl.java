package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

/**
 * Created by Bálint on 2017.03.13..
 */
@Getter
@ToString
public class TableViewImpl implements TableView {

    private TableAdapter adapter;
    private long id;
    private boolean open;
    private RecentConsumption recentConsumption;
    private boolean isVisible;
    private boolean isDisplayable;
    private boolean isNormal;
    private boolean isConsumer;
    private boolean isConsumed;
    private boolean isHost;
    private boolean isHosted;
    private boolean isLoiterer;
    private boolean isFrequenter;
    private boolean isEmployee;
    private boolean canBeHosted;

    private TableType type;
    private int number;
    private int displayedNumber;
    private int guestCount;
    private int capacity;
    private int totalPrice;

    private String name;
    private String note;

    private Point2D position;
    private Dimension2D dimension;

    private TableView consumer;
    private List<TableView> consumedTables;

    private TableView host;
    private List<TableView> hostedTables;

    public TableViewImpl(TableAdapter table) {
        this.adapter = table;
        id = table.getAdaptee().getId();
        open = ReceiptAdapter.getOpenReceipt(table.getAdaptee().getNumber()) != null;
        recentConsumption = initHasRecentConsumption(table);
        isVisible = table.getAdaptee().isVisible();
        isDisplayable = TableType.isDisplayable(table.getAdaptee().getType());
        isNormal = table.getAdaptee().getType().equals(TableType.NORMAL);
        isConsumer = table.isConsumerTable();
        isConsumed = table.isTableConsumed();
        isHost = table.isTableHost();
        isHosted = table.isTableHosted();
        isLoiterer = table.getAdaptee().getType().equals(TableType.LOITERER);
        isFrequenter = table.getAdaptee().getType().equals(TableType.FREQUENTER);
        isEmployee = table.getAdaptee().getType().equals(TableType.EMPLOYEE);
        canBeHosted = TableType.canBeHosted(table.getAdaptee().getType());

        type = table.getAdaptee().getType();
        number = table.getAdaptee().getNumber();
        displayedNumber = initDisplayedNumber(table);
        guestCount = table.getAdaptee().getGuestCount();
        capacity = table.getAdaptee().getCapacity();
        totalPrice = initTotalPrice(table);

        name = table.getAdaptee().getName();
        note = table.getAdaptee().getNote();

        position = initPosition(table);
        dimension = initDimension(table);

        consumer = initConsumer(table);
        consumedTables = initConsumedTables(table);

        host = initHost(table);
        hostedTables = initHostedTables(table);
    }

    private RecentConsumption initHasRecentConsumption(TableAdapter adapter) {
        ReceiptAdapterBase openReceipt = adapter.getOpenReceipt();
        if(openReceipt == null) {
            return RecentConsumption.NO_RECENT;
        }
        LocalDateTime latestSellTime = adapter.getOpenReceipt().getLatestSellTime();
        if(latestSellTime.isAfter(now().minusMinutes(10))) {
            return RecentConsumption.RECENT_10_MINUTES;
        } else if(latestSellTime.isAfter(now().minusMinutes(30))) {
            return RecentConsumption.RECENT_30_MINUTES;
        } else {
            return RecentConsumption.NO_RECENT;
        }
    }

    private int initDisplayedNumber(TableAdapter adapter) {
        if(adapter.getHost() != null) {
            return adapter.getAdaptee().getHost().getNumber();
        }
        return adapter.getAdaptee().getNumber();
    }

    private int initTotalPrice(TableAdapter adapter) {
        ReceiptAdapterBase openReceipt = ((ReceiptAdapterBase)ReceiptAdapter.getOpenReceipt(adapter.getAdaptee().getNumber()));
        if(adapter == null || openReceipt == null ) {
            return 0;
        }
        return openReceipt.getTotalPrice();
    }

    private Point2D initPosition(TableAdapter adapter) {
        return new Point2D(adapter.getAdaptee().getCoordinateX(), adapter.getAdaptee().getCoordinateY());
    }

    private Dimension2D initDimension(TableAdapter adapter) {
        return new Dimension2D(adapter.getAdaptee().getDimensionX(), adapter.getAdaptee().getDimensionY());
    }

    private TableViewImpl initConsumer(TableAdapter table) {
        if(table.getConsumer() == null) {
            return null;
        }
        return new TableViewImpl(table.getConsumer());
    }

    private List<TableView> initConsumedTables(TableAdapter adapter) {
        return adapter.getConsumedTables()
                .stream()
                .map(TableAdapter::new)
                .map(TableViewImpl::new)
                .collect(Collectors.toList());
    }

    private TableView initHost(TableAdapter adapter) {
        if(adapter.getHost() == null) {
            return null;
        }
        return new TableViewImpl(adapter.getHost());
    }

    private List<TableView> initHostedTables(TableAdapter adapter) {
        return adapter.getHostedTables()
                .stream()
                .map(TableAdapter::new)
                .map(TableViewImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isOrderDelivered() {
        ReceiptAdapterBase openReceipt = adapter.getOpenReceipt();
        if (openReceipt == null) {
            return true;
        }
        return openReceipt.isOrderDelivered();
    }

    @Override
    public LocalDateTime getOrderDeliveryTime() {
        ReceiptAdapterBase openReceipt = adapter.getOpenReceipt();
        if (openReceipt == null) {
            return null;
        }
        return openReceipt.getOrderDeliveryTime();
    }

    @Override
    public String toString() {
        return adapter.toString();
    }

    public boolean isConsumer() {
        return isConsumer;
    }

    @Override
    public boolean isHost() {
        return isHost;
    }

    @Override
    public boolean canBeHosted() {
        return canBeHosted;
    }
}
