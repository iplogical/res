package com.inspirationlogical.receipt.utility;

import static java.util.Arrays.asList;

import java.util.function.Predicate;

public class PredicateOperations {

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }

    public static <T> Predicate and(Predicate<T>... predicates) {
        return asList(predicates).stream().reduce(Predicate::and).get();
    }

    public static <T> Predicate or(Predicate<T>... predicates) {
        return asList(predicates).stream().reduce(Predicate::or).get();
    }
}
