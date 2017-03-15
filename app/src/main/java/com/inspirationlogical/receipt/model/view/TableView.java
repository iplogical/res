package com.inspirationlogical.receipt.model.view;

import java.util.Collection;

import com.inspirationlogical.receipt.model.enums.TableType;

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

    int getCapacity();

    String getName();

    String getNote();

    Point2D getPosition();
}
