package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.waiter.controller.TestFXBase;

public abstract class AbstractUtils {
    static protected TestFXBase robot;

    public static void setRobot(TestFXBase robot) {
        AbstractUtils.robot = robot;
    }

}
