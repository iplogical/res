package com.inspirationlogical.receipt.corelib.params;

import javafx.geometry.Dimension2D;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TableParams {
    private String name;

    private Integer number;

    private String note;

    private Integer guestCount;

    private Integer capacity;

    Dimension2D dimension;
}
