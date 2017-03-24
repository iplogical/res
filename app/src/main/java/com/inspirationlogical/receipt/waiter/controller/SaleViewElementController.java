package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.model.view.AbstractView;
import javafx.fxml.Initializable;

/**
 * Created by Bálint on 2017.03.23..
 */
public interface SaleViewElementController<T extends AbstractView> extends Controller {

    void setView(T view);
}
