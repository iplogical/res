package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Getter
@ToString
public class TableViewImpl implements TableView {

    private long id;
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

    private String name;
    private String note;

    private Point2D position;
    private Dimension2D dimension;

    private TableView consumer;
    private List<TableView> consumedTables;

    private TableView host;
    private List<TableView> hostedTables;

    public TableViewImpl(Table table) {
        id = table.getId();
        isVisible = table.isVisible();
        isDisplayable = TableType.isDisplayable(table.getType());
        isNormal = table.getType().equals(TableType.NORMAL);
        isConsumer = isConsumerTable(table);
        isConsumed = isTableConsumed(table);
        isHost = isTableHost(table);
        isHosted = isTableHosted(table);
        isLoiterer = table.getType().equals(TableType.LOITERER);
        isFrequenter = table.getType().equals(TableType.FREQUENTER);
        isEmployee = table.getType().equals(TableType.EMPLOYEE);
        canBeHosted = TableType.canBeHosted(table.getType());

        type = table.getType();
        number = table.getNumber();
        displayedNumber = initDisplayedNumber(table);
        guestCount = table.getGuestCount();
        capacity = table.getCapacity();

        name = table.getName();
        note = table.getNote();

        position = initPosition(table);
        dimension = initDimension(table);

        consumer = initConsumer(table);
        consumedTables = initConsumedTables(table);

        host = initHost(table);
        hostedTables = initHostedTables(table);
    }

    private boolean isConsumerTable(Table table) {
        return !table.getConsumed().isEmpty();
    }

    private boolean isTableConsumed(Table table) {
        return table.getConsumer() != null;
    }

    private boolean isTableHost(Table table) {
        return !table.getHosted().isEmpty();
    }

    private boolean isTableHosted(Table table) {
        return table.getHost() != null;
    }

    private int initDisplayedNumber(Table table) {
        if (table.getHost() != null) {
            return table.getHost().getNumber();
        }
        return table.getNumber();
    }

    private Point2D initPosition(Table adapter) {
        return new Point2D(adapter.getCoordinateX(), adapter.getCoordinateY());
    }

    private Dimension2D initDimension(Table adapter) {
        return new Dimension2D(adapter.getDimensionX(), adapter.getDimensionY());
    }

    private TableViewImpl initConsumer(Table table) {
        if (table.getConsumer() == null) {
            return null;
        }
        return new TableViewImpl(table.getConsumer());
    }

    private List<TableView> initConsumedTables(Table table) {
        return table.getConsumed()
                .stream()
                .map(TableViewImpl::new)
                .collect(Collectors.toList());
    }

    private TableView initHost(Table table) {
        if (table.getHost() == null) {
            return null;
        }
        return new TableViewImpl(table.getHost());
    }

    private List<TableView> initHostedTables(Table table) {
        return table.getHosted()
                .stream()
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
