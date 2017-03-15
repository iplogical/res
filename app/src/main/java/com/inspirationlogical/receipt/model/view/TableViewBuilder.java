package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.enums.TableType;
import javafx.geometry.Point2D;
import lombok.Getter;

/**
 * Created by Bálint on 2017.03.15..
 */

public class TableViewBuilder {
    private @Getter TableType type;
    private @Getter int tableNumber;
    private @Getter String name = "";
    private @Getter Point2D position = null;
    private @Getter int guestNumber = 0;
    private @Getter int tableCapacity = 0;
    private @Getter String note = "";
    private @Getter boolean visibility = false;

    public TableViewBuilder(TableType type, int tableNumber) {
        this.type = type;
        this.tableNumber = tableNumber;
    }

    public TableViewBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TableViewBuilder position(Point2D position) {
        this.position = position;
        return this;
    }

    public TableViewBuilder guestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
        return this;
    }

    public TableViewBuilder tableCapacity(int tableCapacity) {
        this.tableCapacity = tableCapacity;
        return this;
    }

    public TableViewBuilder note(String note) {
        this.note = note;
        return this;
    }

    public TableViewBuilder visibility(boolean visibility) {
        this.visibility = visibility;
        return this;
    }
}
