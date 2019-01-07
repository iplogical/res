package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class TableView {

//    private long id;

    private TableType type;
    private int number;
    private int guestCount;
    private int capacity;

    private String name;
    private String note;

    private int coordinateX;
    private int coordinateY;

    private int height;
    private int width;

    private boolean orderDelivered;
    private LocalDateTime orderDeliveryTime;
}
