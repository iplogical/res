package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.InputStream;

import com.google.inject.Inject;
import lombok.Getter;

/**
 * Created by Ferenc on 2017. 03. 18..
 */
public class PrintService {

    @Getter
    @Inject
    private Printer printer;

    private PrintService(){
        printer = new NullPrinter();
    }

    public String getName(){
        return printer.getName();
    }

    public void print(InputStream pdf){
        printer.print(pdf);
    }

    public static PrintService create(){
        return PrintingInjector.getInjector().getInstance(PrintService.class);
    }
}


