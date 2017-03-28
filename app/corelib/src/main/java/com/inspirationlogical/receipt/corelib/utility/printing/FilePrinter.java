package com.inspirationlogical.receipt.corelib.utility.printing;

import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.corelib.utility.printing.Printer;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
public class FilePrinter implements Printer {
    @Override
    public String getName() {
        return "FilePrinter";
    }

    @Override
    public void print(InputStream pdf) {
        File out = null;
        FileOutputStream outStream = null;
        try {
            String filename = "receipt_" + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")) + ".pdf";
            String path = Resources.CONFIG.getString("ReceiptOutDir") + File.separator + filename;
            new File(Resources.CONFIG.getString("ReceiptOutDir") ).mkdirs();
            out = new File(Paths.get(path).toString());
            out.createNewFile();
            outStream = new FileOutputStream(out);
            IOUtils.copy(pdf,outStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (outStream != null) outStream.close();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}