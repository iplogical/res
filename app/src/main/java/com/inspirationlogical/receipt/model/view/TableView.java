package com.inspirationlogical.receipt.model.view;

import javafx.geometry.Point2D;

import java.util.Collection;

/**
 * Created by Bálint on 2017.03.13..
 */
public interface TableView {
    boolean isOpen();

    boolean isVisible();

    boolean isVirtual();

    int getTableNumber();

    int getGuestNumber();

    int getCapacity();

    String getName();

    String getNote();

    Point2D getCoordinates();

    Collection<ReceiptRecordView> getSoldProducts();
}
