package com.inspirationlogical.receipt.model.view;

import java.util.Collection;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;
import com.inspirationlogical.receipt.model.enums.TableType;

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
        return adapter.getActiveReceipt().getAdaptee().getStatus().equals(ReceiptStatus.OPEN);
    }

    @Override
    public boolean isVisible() {
        return adapter.getAdaptee().isVisibility();
    }

    @Override
    public boolean isVirtual() {
        return adapter.getAdaptee().getType().equals(TableType.VIRTUAL);
    }

    @Override
    public TableType getType() {
        return adapter.getAdaptee().getType();
    }

    @Override
    public int getTableNumber() {
        return adapter.getAdaptee().getNumber();
    }

    @Override
    public int getGuestCount() {
        return adapter.getAdaptee().getGuestNumber();
    }

    @Override
    public int getCapacity() {
        return adapter.getAdaptee().getCapacity();
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
}
