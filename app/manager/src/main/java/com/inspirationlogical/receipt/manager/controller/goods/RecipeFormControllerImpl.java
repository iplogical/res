package com.inspirationlogical.receipt.manager.controller.goods;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.params.RecipeParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.ManagerService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.manager.utility.ManagerResources;
import com.inspirationlogical.receipt.manager.viewmodel.GoodsTableViewModel;
import com.inspirationlogical.receipt.manager.viewmodel.ProductStringConverter;
import com.inspirationlogical.receipt.manager.viewmodel.RecipeViewModel;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addFormDragAndDrop;
import static java.util.stream.Collectors.toList;

@FXMLController
public class RecipeFormControllerImpl extends AbstractController implements RecipeFormController {

    @FXML
    private VBox root;
    @FXML
    private TableView<RecipeViewModel> componentTable;
    @FXML
    private TableColumn<RecipeViewModel, String> componentName;
    @FXML
    private TableColumn<RecipeViewModel, String> componentQuantity;
    @FXML
    private TableColumn<RecipeViewModel, String> componentUnit;
    @FXML
    private ChoiceBox<ProductView> product;
    @FXML
    private ChoiceBox<ProductView> component;
    @FXML
    private TextField quantity;
    @FXML
    private Label quantityUnit;

    @Autowired
    private GoodsController goodsController;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ManagerService managerService;

    private ObservableList<ProductView> sellableProducts;

    private ObservableList<ProductView> storalbeProducts;

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addFormDragAndDrop(root);
        initProductChoiceBox();
        initComponentChoiceBox();
        initComponentTable();
    }

    private void initProductChoiceBox() {
        sellableProducts = FXCollections.observableArrayList(commonService.getSellableProducts());
        sellableProducts.sort(Comparator.comparing(ProductView::getShortName));
        product.setItems(sellableProducts);
        product.getSelectionModel().selectedItemProperty().addListener(new ProductChoiceBoxListener());
        product.setConverter(new ProductStringConverter(sellableProducts));
    }

    private void initComponentChoiceBox() {
        storalbeProducts = FXCollections.observableArrayList(commonService.getStorableProducts());
        storalbeProducts.sort(Comparator.comparing(ProductView::getShortName));
        component.setItems(storalbeProducts);
        component.setConverter(new ProductStringConverter(storalbeProducts));
        component.getSelectionModel().selectedItemProperty().addListener(new ComponentChoiceBoxListener());
    }

    private void initComponentTable() {
        componentTable.setEditable(true);
        initColumn(componentName, RecipeViewModel::getComponentLongName);
        initColumn(componentQuantity, RecipeViewModel::getQuantity);
        initInputColumn(componentQuantity, this::onModifyQuantity);
        initColumn(componentUnit, RecipeViewModel::getUnit);
    }

    private void onModifyQuantity(RecipeViewModel recipe, String newQuantity) {
        try {
            Double.valueOf(newQuantity);
            recipe.setQuantity(newQuantity);
            updateRecipe();
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("RecipeForm.NumberFormatQuantity"));
        }
    }

    @Override
    public void initProducts() {
        initProductChoiceBox();
        initComponentChoiceBox();
        initComponentTable();
    }

    @Override
    public void loadRecipeForm(GoodsController goodsController) {
        this.goodsController = goodsController;
    }

    @Override
    public void setSelectedProduct(GoodsTableViewModel selectedGoodsValue) {
        ProductView selected = product.getConverter().fromString(selectedGoodsValue.getShortName());
        product.getSelectionModel().select(selected);
    }

    @Override
    public void updateComponentsTable() {
        if (!noProductSelected()) {
            updateComponentsTable(product.getValue());
        }
    }

    @FXML
    public void onClose(Event event) {
        goodsController.updateGoods();
        clearRecipeForm();
        goodsController.hideRecipeForm();
    }

    private void clearRecipeForm() {
        componentTable.getItems().clear();
        product.getSelectionModel().clearSelection();
        component.getSelectionModel().clearSelection();
        quantity.clear();
    }

    @FXML
    public void onAdd(Event event) {
        if (noProductSelected() || noComponentSelected()) {
            ErrorMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("RecipeForm.EmptyChoiceBox"));
            return;
        }
        if (componentAlreadyAdded()) {
            ErrorMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("RecipeForm.ComponentAlreadyAdded"));
            return;
        }
        try {
            RecipeViewModel newComponent = buildRecipeViewModel();
            componentTable.getItems().add(newComponent);
            updateRecipe();
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("RecipeForm.NumberFormatQuantity"));
        }

    }

    private boolean noComponentSelected() {
        return getSelectedComponent() == null;
    }

    private ProductView getSelectedComponent() {
        return component.getSelectionModel().getSelectedItem();
    }

    private boolean noProductSelected() {
        return getSelectedProduct() == null;
    }

    private boolean componentAlreadyAdded() {
        return componentTable.getItems().stream()
                .filter(recipeViewModel -> recipeViewModel.getComponentLongName().equals(getSelectedComponent().getLongName()))
                .count() != 0;
    }

    private RecipeViewModel buildRecipeViewModel() {
        ProductView selectedComponent = getSelectedComponent();
        return RecipeViewModel.builder()
                .componentLongName(selectedComponent.getLongName())
                .unit(selectedComponent.getQuantityUnit().toString())
                .quantity(Double.valueOf(quantity.getText()).toString())
                .build();
    }

    private void updateRecipe() {
        List<RecipeParams> recipeParamsList = buildRecipeParamsList();
        managerService.updateRecipe(getSelectedProduct(), recipeParamsList);
    }

    private ProductView getSelectedProduct() {
        return product.getSelectionModel().getSelectedItem();
    }

    private List<RecipeParams> buildRecipeParamsList() {
        return componentTable.getItems().stream()
                .map(recipeViewModel -> RecipeParams.builder()
                        .componentName(recipeViewModel.getComponentLongName())
                        .quantity(Double.valueOf(recipeViewModel.getQuantity()))
                        .build())
                .collect(toList());
    }

    @FXML
    public void onDelete(Event event) {
        if (componentTable.getSelectionModel().getSelectedItem() == null) {
            ErrorMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("RecipeForm.SelectComponentForDelete"));
            return;
        }
        if (componentTable.getItems().size() == 1) {
            ErrorMessage.showErrorMessage(root, ManagerResources.MANAGER.getString("RecipeForm.DeleteLastComponent"));
            return;
        }
        componentTable.getItems().remove(componentTable.getSelectionModel().getSelectedItem());
        updateRecipe();
    }

    private class ProductChoiceBoxListener implements ChangeListener<ProductView> {
        @Override
        public void changed(ObservableValue<? extends ProductView> observable, ProductView oldValue, ProductView newValue) {
            if (newValue != null) {
                updateComponentsTable(newValue);
            }
        }
    }

    private void updateComponentsTable(ProductView product) {
        ObservableList<RecipeViewModel> items =
                FXCollections.observableArrayList(managerService.getRecipeComponents(product).stream().map(RecipeViewModel::new).collect(toList()));
        componentTable.setItems(items);
    }

    private class ComponentChoiceBoxListener implements ChangeListener<ProductView> {
        @Override
        public void changed(ObservableValue<? extends ProductView> observable, ProductView oldValue, ProductView newValue) {
            if (newValue != null) {
                quantityUnit.setText(newValue.getQuantityUnit().toString());
            }
        }

    }
}
