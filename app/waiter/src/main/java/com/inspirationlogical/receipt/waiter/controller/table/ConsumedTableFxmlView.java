package com.inspirationlogical.receipt.waiter.controller.table;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import org.springframework.context.annotation.Scope;

@FXMLView(value = "/view/fxml/ConsumedTable.fxml", bundle = "properties.waiter")
@Scope("prototype")
public class ConsumedTableFxmlView extends AbstractFxmlView {
}