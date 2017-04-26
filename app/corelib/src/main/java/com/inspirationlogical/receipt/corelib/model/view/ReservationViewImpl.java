package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ReservationAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.Reservation;

/**
 * Created by TheDagi on 2017. 04. 26..
 */
public class ReservationViewImpl extends AbstractModelViewImpl<ReservationAdapter>
    implements ReservationView {

    public ReservationViewImpl(ReservationAdapter adapter) {
        super(adapter);
    }

    @Override
    public String getName() {
        return adapter.getAdaptee().getName();
    }

    @Override
    public String getTableNumber() {
        return String.valueOf(adapter.getAdaptee().getTableNumber());
    }

    @Override
    public String getGuestCount() {
        return String.valueOf(adapter.getAdaptee().getGuestCount());
    }

    @Override
    public String getNote() {
        return adapter.getAdaptee().getNote();
    }

    @Override
    public String getDate() {
        return adapter.getAdaptee().getDate().toString();
    }

    @Override
    public String getStartTime() {
        return adapter.getAdaptee().getStartTime().toString();
    }

    @Override
    public String getEndTime() {
        return adapter.getAdaptee().getEndTime().toString();
    }
}
