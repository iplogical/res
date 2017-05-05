package com.inspirationlogical.receipt.corelib.frontend.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.time.LocalTime;

public interface Controller extends Initializable{
    String getViewPath();
    Node getRootNode();

    default void initLiveTime(Label liveTime) {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, event -> {
            LocalTime time = LocalTime.now();
            String hour = time.getHour() < 10 ? ("0" + String.valueOf(time.getHour())) : (String.valueOf(time.getHour()));
            String minute = time.getMinute() < 10 ? ("0" + String.valueOf(time.getMinute())) : (String.valueOf(time.getMinute()));
            String second = time.getSecond() < 10 ? ("0" + String.valueOf(time.getSecond())) : (String.valueOf(time.getSecond()));
            liveTime.setText(hour  + ":" + minute  + ":" + second);
        }),
                new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
