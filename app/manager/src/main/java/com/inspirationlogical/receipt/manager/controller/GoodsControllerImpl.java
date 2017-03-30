package com.inspirationlogical.receipt.manager.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.inspirationlogical.receipt.manager.viewmodel.ProductViewModel;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TreeTableView;

public class GoodsControllerImpl implements GoodsController {

    public static final String GOODS_VIEW_PATH = "/view/fxml/Goods.fxml";

    @FXML
    protected TreeTableView<ProductViewModel> products;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public Node getRootNode() {
        return null;
    }
}
