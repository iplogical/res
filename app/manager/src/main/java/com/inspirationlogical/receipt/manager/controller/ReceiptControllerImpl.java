package com.inspirationlogical.receipt.manager.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.manager.viewmodel.ReceiptViewModel;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.BorderPane;

@Singleton
public class ReceiptControllerImpl extends AbstractController implements ReceiptController {

    public static final String GOODS_VIEW_PATH = "/view/fxml/Goods.fxml";

    @FXML
    private BorderPane root;
    @FXML
    TreeTableView<ReceiptViewModel> receiptsTable;
    @FXML
    Button showGoods;

    @Inject
    private ViewLoader viewLoader;

    private CommonService commonService;

    @Inject
    public ReceiptControllerImpl(CommonService commonService) {
        this.commonService = commonService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initColumns();
    }

    @Override
    public String getViewPath() {
        return GOODS_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @FXML
    public void onShowGoods(Event event) {
        viewLoader.loadViewIntoScene(null);
    }

    private void initColumns() {
    }
}
