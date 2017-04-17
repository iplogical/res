package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.DailyClosure;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;

/**
 * Created by TheDagi on 2017. 04. 17..
 */
public class DailyClosureAdapter extends AbstractAdapter<DailyClosure> {

    public DailyClosureAdapter(DailyClosure adaptee) {
        super(adaptee);
    }

    public static LocalDateTime getLatestClosureTime() {
        List<DailyClosure> dailyClosureList = GuardedTransaction.runNamedQuery(DailyClosure.GET_LATEST_DAILY_CLOSURE);
        if(dailyClosureList.isEmpty()) {
            return now().minusDays(1);
        }
        return dailyClosureList.get(0).getClosureTime();
    }
}
