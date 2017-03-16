package com.inspirationlogical.receipt.corelib.utility;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import com.google.common.io.Files;
import org.apache.fop.apps.*;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class ReceiptXMLToPDFEpsonTMT20II implements  ReceiptXMLtoPDF {

    private String xslTemplate;
    ReceiptXMLToPDFEpsonTMT20II(){
        try {
            xslTemplate = Files.toString(
                    Paths.get(Resources.CONFIG.getString("ReceiptXSLTPath")).toFile(),
                    Charset.defaultCharset());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    /**
     * Method that will convert the given XML to PDF
     */
    @Override
    public void convertToPDF(OutputStream out, InputStream xml) {
        InputStream xsl = new ByteArrayInputStream(xslTemplate.getBytes(Charset.defaultCharset()));
        StreamSource xmlSource = new StreamSource(xml);
        // create an instance of fop factory
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        // a user agent is needed for transformation
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        try {
            // Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsl));

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            // That's where the XML is first transformed to XSL-FO and then
            // PDF is created
            transformer.transform(xmlSource, res);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
