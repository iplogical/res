package com.inspirationlogical.receipt.service;

import lombok.Data;

public @Data class Pair<A, B> {

    private A first;
    private B second;

    public Pair(final A first, final B second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Pair cannot contain null values");
        }
        this.first = first;
        this.second = second;
    }

    @Override
    public final String toString() {
        return "<" + first.toString() + ":" + second.toString() + ">";
    }

}
