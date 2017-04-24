package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.adapter.TableAdapter;
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
        return adapter.getActiveReceipt() != null;
    }

    @Override
    public boolean isVisible() {
        return adapter.getAdaptee().isVisible();
    }

    @Override
    public boolean isAggregate() {
        return adapter.getAdaptee().getType().equals(TableType.AGGREGATE);
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
    public int getNumber() {
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
    public Dimension2D getDimension() {
        return new Dimension2D(adapter.getAdaptee().getDimensionX(), adapter.getAdaptee().getDimensionY());
    }

    @Override
    public List<TableView> getConsumedTables() {
        return adapter.getConsumedTables()
                .stream()
                .map(TableAdapter::new)
                .map(TableViewImpl::new)
                .collect(Collectors.toList());
    }
}
