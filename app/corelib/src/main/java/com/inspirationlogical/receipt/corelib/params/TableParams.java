package com.inspirationlogical.receipt.corelib.params;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
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

    private Dimension2D dimension;
}
