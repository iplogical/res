package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import javafx.scene.input.KeyEvent;

import static com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleViewState.CancellationType.*;

public class SaleControllerInitializer {

    private SaleControllerImpl s;

    SaleControllerInitializer(SaleControllerImpl s) {
        this.s = s;
    }

    public void initialize() {
        initializeProductController();
        initializeToggles();
        initializeQuickSearchAndSellHandler();
        s.initLiveTime(s.liveTime);
    }

    private void initializeProductController() {
        s.productController.setCategoriesGrid(s.categoriesGrid);
        s.productController.setSubCategoriesGrid(s.subCategoriesGrid);
        s.productController.setProductsGrid(s.productsGrid);
//        s.productController.setSaleController(s);
//        s.productController.setViewLoader(s.getViewLoader());
    }

    private void initializeToggles() {
        s.singleCancellation.setUserData(SINGLE);
        s.selectiveCancellation.setUserData(SELECTIVE);
        s.saleViewState.setCancellationType(NONE);
        s.giftProduct.selectedProperty().addListener(s.giftProductToggleListener);
        s.cancellationTypeToggleGroup.selectedToggleProperty().addListener(s.cancellationTypeToggleListener);
        s.sortByClickTime.selectedProperty().addListener(s.sortByClickTimeToggleListener);
        s.takeAway.selectedProperty().addListener(s.takeAwayChangeListener);
    }

    private void initializeQuickSearchAndSellHandler() {
        s.rootSale.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    if (s.productController.getSearchedProducts().size() == 1) {
                        s.sellProduct(s.productController.getSearchedProducts().get(0));
                    }
                    break;
                case DELETE:
                    s.searchField.clear();
                    s.productController.updateCategoriesAndProducts();
                    break;
                case BACK_SPACE:
                    if (searchFieldNotEmpty()) {
                        s.searchField.setText(s.searchField.getText(0, s.searchField.getText().length() - 1));
                        s.onSearchChanged(keyEvent);
                    }
                    break;
                default:
                    s.searchField.appendText(keyEvent.getText());
                    s.onSearchChanged(keyEvent);
                    break;
            }
        });
    }

    private boolean searchFieldNotEmpty() {
        return !s.searchField.getText().isEmpty();
    }
}
