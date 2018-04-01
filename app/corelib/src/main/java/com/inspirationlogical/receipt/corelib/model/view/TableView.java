package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Bálint on 2017.03.13..
 */
public interface TableView extends AbstractView {

    long getId();

    boolean isVisible();

    boolean isDisplayable();

    boolean isNormal();

    boolean isConsumer();

    boolean isConsumed();

    boolean isHost();

    boolean isHosted();

    boolean isLoiterer();

    boolean isFrequenter();

    boolean isEmployee();

    boolean canBeHosted();

    TableType getType();

    int getNumber();

    int getDisplayedNumber();

    int getGuestCount();

    int getCapacity();

    String getName();

    String getNote();

    Point2D getPosition();

    Dimension2D getDimension();

    TableView getConsumer();

    List<TableView> getConsumedTables();

    TableView getHost();

    List<TableView> getHostedTables();

    boolean isOrderDelivered();

    LocalDateTime getOrderDeliveryTime();
}
