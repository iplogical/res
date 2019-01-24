package com.inspirationlogical.receipt.corelib.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class AbstractService {

    EntityViews entityViews;

    AbstractService(EntityViews entityViews) {
        this.entityViews = entityViews;
    }
}
