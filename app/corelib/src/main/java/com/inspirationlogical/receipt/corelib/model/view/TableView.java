package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

/**
 * Created by Bálint on 2017.03.13..
 */
public interface TableView extends AbstractView {

    boolean isOpen();

    boolean isVisible();

    boolean isVirtual();

    TableType getType();

    int getNumber();

    int getGuestCount();

    int getCapacity();

    int getTotalPrice();

    String getName();

    String getNote();

    Point2D getPosition();

    Dimension2D getDimension();

    List<TableView> getConsumedTables();
}
