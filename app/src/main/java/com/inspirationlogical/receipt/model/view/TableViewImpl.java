package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.TableAdapter;
import javafx.geometry.Point2D;

import java.util.Collection;

/**
 * Created by Bálint on 2017.03.13..
 */
public class TableViewImpl extends AbstractModelViewImpl<TableAdapter> implements TableView {

    public TableViewImpl(TableAdapter adapter) {
        super(adapter);
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    public int getTableNumber() {
        return 0;
    }

    @Override
    public int getGuestNumber() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getNote() {
        return null;
    }

    @Override
    public Point2D getCoordinates() {
        return null;
    }

    @Override
    public Collection<ReceiptRecordView> getSoldProducts() {
        return null;
    }
}
