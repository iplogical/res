package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;
import javafx.geometry.Point2D;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractUtils {
    @Setter
    static protected TestFXBase robot;

    protected static final List<Point2D> SOLD_POINTS = new ArrayList<>(Arrays.asList(
            new Point2D(150, 175),
            new Point2D(150, 205),
            new Point2D(150, 235),
            new Point2D(150, 265)));

    protected static final List<Point2D> PAID_POINTS = new ArrayList<>(Arrays.asList(
            new Point2D(700, 175),
            new Point2D(700, 205),
            new Point2D(700, 235),
            new Point2D(700, 265)));


}
