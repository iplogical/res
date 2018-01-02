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

    public static PrintService create(){
        try {
            return PrintingInjector.getInjector().getInstance(PrintService.class);
        }catch (Exception e){
            return null;
        }
    }

    public String getName(){
        return printer.getName();
    }

    public void print(InputStream pdf){
        printer.print(pdf);
    }
}


