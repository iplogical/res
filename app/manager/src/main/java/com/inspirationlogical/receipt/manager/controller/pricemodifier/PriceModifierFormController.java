package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.model.view.PriceModifierView;
import com.inspirationlogical.receipt.manager.viewmodel.PriceModifierViewModel;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
public interface PriceModifierFormController extends Controller {
    void loadPriceModifierForm(PriceModifierController priceModifierController);

    void loadPriceModifierForm(PriceModifierViewModel selected);

    void clearPriceModifierForm();
}
