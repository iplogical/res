package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.manager.viewmodel.*;

public interface PriceModifierFormController extends Controller {
    void loadPriceModifierForm(PriceModifierController priceModifierController);

    void loadPriceModifierForm(PriceModifierViewModel selected);

    void clearPriceModifierForm();
}
