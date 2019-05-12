package com.inspirationlogical.receipt.corelib.frontend.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by TheDagi on 2017. 04. 15..
 */
public class AbstractController {

    private <T> T getViewModel(TableColumn.CellDataFeatures<T, String> cellDataFeatures) {
        return cellDataFeatures.getValue();
    }

    private <T> T getViewModel(TreeTableColumn.CellDataFeatures<T, String> cellDataFeatures) {
        return cellDataFeatures.getValue().getValue();
    }

    protected <T> void initColumn(TableColumn<T, String> tableColumn, Function<T, String> method) {
        tableColumn.setCellValueFactory((TableColumn.CellDataFeatures<T, String> category) ->
                new ReadOnlyStringWrapper(method.apply(getViewModel(category))));
    }

    protected <T> void initColumn(TreeTableColumn<T, String> tableColumn, Function<T, String> method) {
        tableColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<T, String> category) ->
                new ReadOnlyStringWrapper(method.apply(getViewModel(category))));
    }

    protected <T> void initInputColumn(TableColumn<T, String> tableColumn, BiConsumer<T, String> method) {
        tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tableColumn.setOnEditCommit(event -> method.accept(event.getRowValue(), event.getNewValue()));
    }
}
