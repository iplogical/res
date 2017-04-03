package com.inspirationlogical.receipt.manager.controller;

import static com.inspirationlogical.receipt.manager.controller.GoodsControllerImpl.GOODS_VIEW_PATH;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.manager.application.ManagerApp;
import com.inspirationlogical.receipt.manager.viewmodel.StockViewModel;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

@Singleton
public class StockControllerImpl implements StockController {

    public static final String STOCK_VIEW_PATH = "/view/fxml/Stock.fxml";

    @FXML
    TableView<StockViewModel> stockTable;
    @FXML
    Button showGoods;

    @Inject
    private ViewLoader viewLoader;

    private GoodsController goodsController;

    @Inject
    public StockControllerImpl(GoodsController goodsController) {
        this.goodsController = goodsController;
    }

    @FXML
    public void onShowGoods(Event event) {
        viewLoader.loadView(ManagerApp.getWindow(), GOODS_VIEW_PATH, goodsController);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public Node getRootNode() {
        return null;
    }
}
