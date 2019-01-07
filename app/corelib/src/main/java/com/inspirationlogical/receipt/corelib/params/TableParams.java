package com.inspirationlogical.receipt.corelib.params;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TableParams {

    private String name;

    private Integer number;

    private String note;

    private Integer guestCount;

    private Integer capacity;

    private TableType type;

    private int positionX;
    private int positionY;

    private int height;
    private int width;
}
