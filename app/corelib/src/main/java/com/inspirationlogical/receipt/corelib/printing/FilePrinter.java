package com.inspirationlogical.receipt.corelib.printing;

import static java.time.LocalDateTime.now;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import lombok.Getter;
import org.apache.commons.io.IOUtils;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

public class FilePrinter implements Printer {

    @Getter
    private static String filePath;

    @Override
    public void print(InputStream pdf) {
        File out = null;
        FileOutputStream outStream = null;
        try {
            String fileName = "receipt_" + now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")) + ".pdf";
            String path = Resources.CONFIG.getString("ReceiptOutDir") + File.separator + fileName;
            new File(Resources.CONFIG.getString("ReceiptOutDir") ).mkdirs();
            filePath = Paths.get(path).toString();
            out = new File(filePath);
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
