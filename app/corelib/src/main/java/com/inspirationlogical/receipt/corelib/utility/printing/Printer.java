package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.InputStream;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
public interface Printer {
    String getName();
    void print(InputStream pdf);
}

