package com.inspirationlogical.receipt.corelib.utility;

public class Wrapper<T> {

    private T content;

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
