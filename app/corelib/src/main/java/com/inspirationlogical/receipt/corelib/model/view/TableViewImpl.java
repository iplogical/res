package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.Orientation;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;

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
        return adapter.getActiveReceipt() != null;
    }

    @Override
    public boolean isVisible() {
        return adapter.getAdaptee().isVisible();
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
    public int getTableCapacity() {
        return adapter.getAdaptee().getCapacity();
    }

    @Override
    public int getTotalPrice() {
        if(adapter == null ) {
            return 0;
        }
        return adapter.getActiveReceipt().getTotalPrice();
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
    public Orientation getOrientation() {
        return adapter.getAdaptee().getOrientation();
    }
}
