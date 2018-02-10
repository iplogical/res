package com.inspirationlogical.receipt.corelib.printing;

import java.io.InputStream;

public interface Printer {
//
//    String getName();

    void print(InputStream pdf);
}

