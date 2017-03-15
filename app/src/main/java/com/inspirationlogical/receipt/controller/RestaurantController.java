package com.inspirationlogical.receipt.controller;

import javafx.fxml.Initializable;

public interface RestaurantController extends Initializable {
    void showAddTableForm();
    void createTable(int number, int capacity, boolean isVirtual);
}
