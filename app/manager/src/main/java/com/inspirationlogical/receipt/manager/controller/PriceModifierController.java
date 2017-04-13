package com.inspirationlogical.receipt.manager.controller;

import com.inspirationlogical.receipt.corelib.frontend.controller.Controller;
import com.inspirationlogical.receipt.corelib.service.PriceModifierParams;

/**
 * Created by régiDAGi on 2017. 04. 08..
 */
public interface PriceModifierController extends Controller {
    void addPriceModifier(PriceModifierParams params);
}
