package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.VATSerieNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;

import java.util.List;

/**
 * Created by Bálint on 2017.03.15..
 */
public class VATSerieAdapter extends AbstractAdapter<VATSerie> {

    public static VATSerieAdapter vatSerieAdapterFactory() {
        List<VATSerie> vatSerieList = EntityManagerProvider.getEntityManager().createNamedQuery(VATSerie.GET_VAT_SERIE).getResultList();
        if (vatSerieList.isEmpty()) {
            throw new VATSerieNotFoundException();
        }
        return new VATSerieAdapter(vatSerieList.get(0));

    }

    public VATSerieAdapter(VATSerie adaptee) {
        super(adaptee);
    }
}
