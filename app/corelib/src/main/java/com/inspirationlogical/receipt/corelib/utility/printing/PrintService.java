package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.InputStream;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Ferenc on 2017. 03. 18..
 */

@NoArgsConstructor
public class PrintService {

    @Getter
    @Inject
    private Printer printer;

    private PrintService(String input){
        printer = new NullPrinter();
    }

    public String getName(){
        return printer.getName();
    }

    public void print(InputStream pdf){
        printer.print(pdf);
    }

    public static PrintService create(){
        try {
            return PrintingInjector.getInjector().getInstance(PrintService.class);
        }catch (Exception e){
            return new PrintService("");
        }
    }
}


