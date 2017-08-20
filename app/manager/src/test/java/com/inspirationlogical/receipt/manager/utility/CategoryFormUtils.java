package com.inspirationlogical.receipt.manager.utility;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.selectChoiceBoxItem;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.setTextField;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;

public class CategoryFormUtils extends AbstractUtils {

    public static void setCategoryName(String name) {
        setTextField(CATEGORY_NAME, name);
    }

    public static void setLeafCategoryType() {
        setCategoryType(2);
    }

    public static void setAggregateCategoryType() {
        setCategoryType(1);
    }

    private static void setCategoryType(int number) {
        selectChoiceBoxItem(CATEGORY_TYPE, number);
    }

    public static void setParentCategory(int number) {
        selectChoiceBoxItem(CATEGORY_PARENT, number);
    }
}
