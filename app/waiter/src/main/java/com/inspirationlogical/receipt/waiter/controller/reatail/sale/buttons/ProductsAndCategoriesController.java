package com.inspirationlogical.receipt.waiter.controller.reatail.sale.buttons;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import javafx.scene.layout.GridPane;

import java.util.List;

public interface ProductsAndCategoriesController {
    void initCategoriesAndProducts();

    void updateCategoriesAndProducts();

    void selectCategory(ProductCategoryView selected);

    void onBackButtonClicked();

    void search(String text);

    List<ProductView> getSearchedProducts();

    void setCategoriesGrid(GridPane categoriesGrid);
    void setSubCategoriesGrid(GridPane subCategoriesGrid);
    void setProductsGrid(GridPane productsGrid);

    ProductView getProductViewBeingDrawn();

    void setProductControllerBeingDrawn(ProductController productControllerBeingDrawn);

    ProductCategoryView getProductCategoryViewBeingDrawn();

    void setCategoryControllerBeingDrawn(CategoryController controllerBeingDrawn);
}
