package com.inspirationlogical.receipt.waiter.controller;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.converter.IntegerStringConverter;
import jfxtras.scene.control.CalendarPicker;
import jfxtras.scene.control.CalendarTimePicker;
import lombok.Getter;

import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;

/**
 * Created by Ferenc on 2017. 04. 17..
 */
public class ReservationFormController implements Controller {
    public static final String VIEW_PATH = "/view/fxml/ReservationForm.fxml ";

    @Getter
    Popup popup;

    @FXML
    VBox root;

    @FXML
    Label title;

    @FXML
    TextField name;

    @FXML
    TextField noOfPeople;

    @FXML
    Button confirm;

    @FXML
    AnchorPane startPlaceholder;

    @FXML
    AnchorPane endPlaceholder;

    @FXML
    TextField note;

    @FXML
    public void onCancel(MouseEvent event) {
        hideNode(root);
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        hideNode(root);
    }

    CalendarPicker start;
    CalendarTimePicker end;

    @Override
    public String getViewPath() {
        return VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root);
        noOfPeople.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    void show(Node parent, Point2D position){
        // TODO: determine position
        double pos_x = 500;
        double pos_y = 500;
        popup.show(parent, pos_x, pos_y);
        popup.getContent().forEach(node -> node.setVisible(true));
    }

    ReservationFormController(ViewLoader viewLoader) {
        popup = new Popup();
        popup.getContent().add(viewLoader.loadView(this));
        start = new CalendarPicker();
        start.setCalendar(Calendar.getInstance());
        start.setShowTime(Boolean.TRUE);
        start.setLocale(Locale.forLanguageTag("hu-HU"));
        start.setCalendar(Calendar.getInstance());
        end = new CalendarTimePicker();
        end.setLocale(Locale.forLanguageTag("hu-HU"));
        end.setCalendar(Calendar.getInstance());
        startPlaceholder.getChildren().add(0,start);
        endPlaceholder.getChildren().add(0,end);
    }
}
