package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.List;

import com.inspirationlogical.receipt.corelib.exception.VATSerieNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
import com.inspirationlogical.receipt.corelib.model.transaction.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.transaction.GuardedTransaction;

/**
 * Created by Bálint on 2017.03.15..
 */
public class VATSerieAdapter extends AbstractAdapter<VATSerie> {

    public static VATSerieAdapter getActiveVATSerieAdapter() {
        List<VATSerie> vatSerieList = GuardedTransaction.runNamedQuery(VATSerie.GET_VAT_SERIE);
        if (vatSerieList.isEmpty()) {
            throw new VATSerieNotFoundException();
        }
        return new VATSerieAdapter(vatSerieList.get(0));

    }

    public VATSerieAdapter(VATSerie adaptee) {
        super(adaptee);
    }
}
