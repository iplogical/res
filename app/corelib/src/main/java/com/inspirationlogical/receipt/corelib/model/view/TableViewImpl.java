package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.receipt.ReceiptAdapterBase;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * Created by Bálint on 2017.03.13..
 */
public class TableViewImpl extends AbstractModelViewImpl<TableAdapter> implements TableView {

    public TableViewImpl(TableAdapter adapter) {
        super(adapter);
    }

    @Override
    public boolean isOpen() {
        return ReceiptAdapter.getOpenReceipt(adapter.getAdaptee().getNumber()) != null;
    }

    @Override
    public boolean isVisible() {
        return adapter.getAdaptee().isVisible();
    }

    @Override
    public boolean isDisplayable() {
        return TableType.isDisplayable(adapter.getAdaptee().getType());
    }

    @Override
    public boolean isNormal() {
        return adapter.getAdaptee().getType().equals(TableType.NORMAL);
    }

    @Override
    public boolean isConsumer() {
        return adapter.isConsumerTable();
    }

    @Override
    public boolean isConsumed() {
        return adapter.isTableConsumed();
    }

    @Override
    public boolean isHost() {
        return adapter.isTableHost();
    }

    @Override
    public boolean isHosted() {
        return adapter.isTableHosted();
    }

    @Override
    public boolean isLoiterer() {
        return adapter.getAdaptee().getType().equals(TableType.LOITERER);
    }

    @Override
    public boolean isFrequenter() {
        return adapter.getAdaptee().getType().equals(TableType.FREQUENTER);
    }

    @Override
    public boolean isEmployee() {
        return adapter.getAdaptee().getType().equals(TableType.EMPLOYEE);
    }

    @Override
    public boolean canBeHosted() {
        return TableType.canBeHosted(adapter.getAdaptee().getType());
    }

    @Override
    public TableType getType() {
        return adapter.getAdaptee().getType();
    }

    @Override
    public int getNumber() {
        return adapter.getAdaptee().getNumber();
    }

    @Override
    public int getDisplayedNumber() {
        if(adapter.getHost() != null) {
            return adapter.getAdaptee().getHost().getNumber();
        }
        return adapter.getAdaptee().getNumber();
    }

    @Override
    public int getGuestCount() {
        return adapter.getAdaptee().getGuestCount();
    }

    @Override
    public int getCapacity() {
        return adapter.getAdaptee().getCapacity();
    }

    @Override
    public int getTotalPrice() {
        if(adapter == null ) {
            return 0;
        }
        return ((ReceiptAdapterBase)ReceiptAdapter.getOpenReceipt(adapter.getAdaptee().getNumber())).getTotalPrice();
    }

    @Override
    public String getName() {
        return adapter.getAdaptee().getName();
    }

    @Override
    public String getNote() {
        return adapter.getAdaptee().getNote();
    }

    @Override
    public Point2D getPosition() {
        return new Point2D(adapter.getAdaptee().getCoordinateX(), adapter.getAdaptee().getCoordinateY());
    }

    @Override
    public Dimension2D getDimension() {
        return new Dimension2D(adapter.getAdaptee().getDimensionX(), adapter.getAdaptee().getDimensionY());
    }
    @Override
    public TableView getConsumer() {
        return new TableViewImpl(adapter.getConsumer());
    }

    @Override
    public List<TableView> getConsumedTables() {
        return adapter.getConsumedTables()
                .stream()
                .map(TableAdapter::new)
                .map(TableViewImpl::new)
                .collect(Collectors.toList());
    }
    @Override
    public TableView getHost() {
        if(adapter.getHost() == null) {
            return null;
        }
        return new TableViewImpl(adapter.getHost());
    }
    @Override
    public List<TableView> getHostedTables() {
        return adapter.getHostedTables()
                .stream()
                .map(TableAdapter::new)
                .map(TableViewImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return adapter.toString();
    }
}
