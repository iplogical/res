package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.enums.TableType;
import javafx.geometry.Point2D;

import java.util.Collection;

/**
 * Created by Bálint on 2017.03.13..
 */
public interface TableView {
    boolean isOpen();

    boolean isVisible();

    boolean isVirtual();

    TableType getType();

    int getTableNumber();

    int getGuestNumber();

    int getCapacity();

    String getName();

    String getNote();

    Point2D getCoordinates();

    Collection<ReceiptRecordView> getSoldProducts();
}
