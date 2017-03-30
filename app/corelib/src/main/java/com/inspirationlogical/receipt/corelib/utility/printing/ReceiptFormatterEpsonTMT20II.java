package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.inspirationlogical.receipt.corelib.exception.FOPCfgXMLFormatException;
import com.inspirationlogical.receipt.corelib.exception.FOPCfgXMLNotFoundException;
import com.inspirationlogical.receipt.corelib.exception.FOPConfigurationErrorException;
import com.inspirationlogical.receipt.corelib.exception.ReceiptXSLTNotFoundException;
import com.inspirationlogical.receipt.corelib.utility.Resources;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class ReceiptFormatterEpsonTMT20II implements ReceiptFormatter {
    private static FopFactory fopFactory;
    static {
        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
        String cfgFilePath = Resources.CONFIG.getString("FopConfigDir") + File.separator + Resources.CONFIG.getString("FopConfigFile");
        Configuration cfg;
        try {
            cfg = cfgBuilder.buildFromFile(new File(cfgFilePath));
        } catch (SAXException e) {
            throw new FOPCfgXMLFormatException(e);
        } catch (IOException e) {
            throw new FOPCfgXMLNotFoundException(e);
        } catch (ConfigurationException e) {
            throw new FOPConfigurationErrorException(e);
        }
        FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(Paths.get(Resources.CONFIG.getString("FopConfigDir")).toUri()).setConfiguration(cfg);
        fopFactory = fopFactoryBuilder.build();
    }

    private String xslTemplate;
    ReceiptFormatterEpsonTMT20II(){
        try {
            xslTemplate = Files.toString(
                    Paths.get(Resources.CONFIG.getString("ReceiptXSLTPath")).toFile(),
                    Charset.defaultCharset());
        } catch (IOException e) {
            throw new ReceiptXSLTNotFoundException(Resources.CONFIG.getString("ReceiptXSLTPath"));
        }
    }
    /**
     * Method that will convert the given XML to PDF
     */
    @Override
    public void convertToPDF(OutputStream out, InputStream xml) {
        InputStream xsl = new ByteArrayInputStream(xslTemplate.getBytes(Charset.defaultCharset()));
        StreamSource xmlSource = new StreamSource(xml);

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
