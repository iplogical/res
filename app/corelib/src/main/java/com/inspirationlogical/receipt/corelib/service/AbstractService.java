package com.inspirationlogical.receipt.corelib.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class AbstractService {

    EntityViews entityViews;

    static <View, Adapter> List<View> createViewsFromAdapters(List<Adapter> adapters, Function<Adapter, View> constructor) {
        return adapters.stream()
                .map(constructor)
                .collect(Collectors.toList());
    }

    AbstractService(EntityViews entityViews) {
        this.entityViews = entityViews;
    }
}
