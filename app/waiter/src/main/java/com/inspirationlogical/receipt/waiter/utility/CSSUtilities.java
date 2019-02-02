package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.model.enums.RecentConsumption;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import javafx.scene.Node;

/**
 * Created by Bálint on 2017.03.24..
 */
public class CSSUtilities {

    private static String BORDER_SELECTED = "#00bfff";
    private static String BORDER_DEFAULT = "#000000";
    private static String BACKGROUND_OPEN = "#98FB98";
    private static String BACKGROUND_RECENT_10_MINUTES = "#FF2121";
    private static String BACKGROUND_RECENT_30_MINUTES = "#FFFC5E";
    private static String BACKGROUND_DEFAULT = "#D2B48C";
    private static String BACKGROUND_CONSUMED = "#00bfff3f";

    private static void setProperty(Node node, String property, String value) {
        node.setStyle(node.getStyle().replaceFirst("(" + property + ":.*?;)", property + ": " + value + ";"));
    }

    private static void setBorderColor(Node node, String color) {
        setProperty(node, "-fx-border-color", color);
    }

    private static void setBorderWidth(Node node, Integer width) {
        setProperty(node, "-fx-border-width", width.toString());
    }

    private static void setBackgroundColor(Node node, String color) {
        setProperty(node, "-fx-background-color", color);
    }

    public static void setBorderColor(boolean isSelected, Node node) {
        if(isSelected) {
            setBorderColor(node, BORDER_SELECTED);
            setBorderWidth(node, 2);
        } else {
            setBorderColor(node, BORDER_DEFAULT);
            setBorderWidth(node, 2);
        }
    }

    public static void setBackgroundColor(TableView tableView, RecentConsumption recentConsumption, Node node) {
        boolean isOrderNotYetDelivered = !tableView.isFoodDelivered() || !tableView.isDrinkDelivered();
        if(isOrderNotYetDelivered && recentConsumption.equals(RecentConsumption.RECENT_10_MINUTES)) {
            setBackgroundColor(node, BACKGROUND_RECENT_10_MINUTES);
        } else if(isOrderNotYetDelivered && recentConsumption.equals(RecentConsumption.RECENT_30_MINUTES)) {
            setBackgroundColor(node, BACKGROUND_RECENT_30_MINUTES);
        } else if(tableView.isOpen()) {
            setBackgroundColor(node, BACKGROUND_OPEN);
        } else {
            setBackgroundColor(node, BACKGROUND_DEFAULT);
        }
    }
}
