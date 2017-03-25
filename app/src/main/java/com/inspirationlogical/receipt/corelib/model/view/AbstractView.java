package com.inspirationlogical.receipt.corelib.model.view;

import java.util.Comparator;

/**
 * Created by Bálint on 2017.03.23..
 */
public interface AbstractView {

    Comparator<AbstractView> compareNames = new Comparator<AbstractView>() {
        @Override
        public int compare(AbstractView o1, AbstractView o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    String getName();
}
