package com.inspirationlogical.receipt.manager.controller.pricemodifier;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
public interface PriceModifierController extends Controller {
    void addPriceModifier(PriceModifierParams params);
}
