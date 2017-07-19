package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import javafx.scene.layout.GridPane;

import java.util.List;

public interface ProductsAndCategoriesController {
    void initCategories();

    void updateCategories();

    void selectCategory(ElementController elementController);

    void search(String text);

    List<ProductView> getSearchedProducts();

    void setCategoriesGrid(GridPane categoriesGrid);
    void setSubCategoriesGrid(GridPane subCategoriesGrid);
    void setProductsGrid(GridPane productsGrid);
    void setSaleController(SaleController saleController);
    void setViewLoader(ViewLoader viewLoader);
}
