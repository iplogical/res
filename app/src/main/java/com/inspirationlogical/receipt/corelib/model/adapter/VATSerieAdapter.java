package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.exception.VATSerieNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by Bálint on 2017.03.15..
 */
public class VATSerieAdapter extends AbstractAdapter<VATSerie> {

    public static VATSerieAdapter vatSerieAdapterFactory(EntityManager manager) {
        List<VATSerie> vatSerieList = manager.createNamedQuery(VATSerie.GET_VAT_SERIE).getResultList();
        if (vatSerieList.isEmpty()) {
            throw new VATSerieNotFoundException();
        }
        return new VATSerieAdapter(vatSerieList.get(0), manager);

    }

    public VATSerieAdapter(VATSerie adaptee, EntityManager manager) {
        super(adaptee, manager);
    }
}
