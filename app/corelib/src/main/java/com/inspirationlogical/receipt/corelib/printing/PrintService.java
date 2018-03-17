package com.inspirationlogical.receipt.corelib.printing;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.InputStream;

@NoArgsConstructor
public class PrintService {

    @Getter
    private Printer printer;

    public static PrintService create(){
//        try {
//            return PrintingInjector.getInjector().getInstance(PrintService.class);
//        }catch (Exception e){
        // TODO_FIX_PRINT
        return null;
//        }
    }

    public void print(InputStream pdf){
        printer.print(pdf);
    }
}


