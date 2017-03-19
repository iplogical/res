package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import javafx.geometry.Point2D;

/**
 * Created by Bálint on 2017.03.13..
 */
public interface TableView {

    boolean isOpen();

    boolean isVisible();

    boolean isVirtual();

    TableType getType();

    int getTableNumber();

    int getGuestCount();

    int getTableCapacity();

    String getName();

    String getNote();

    Point2D getPosition();
}
