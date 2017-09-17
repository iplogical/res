package com.inspirationlogical.receipt.corelib.utility.printing;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.apache.fop.apps.io.ResourceResolverFactory;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.xml.sax.SAXException;

import com.inspirationlogical.receipt.corelib.exception.FOPCfgXMLFormatException;
import com.inspirationlogical.receipt.corelib.exception.FOPCfgXMLNotFoundException;
import com.inspirationlogical.receipt.corelib.exception.FOPConfigurationErrorException;
import com.inspirationlogical.receipt.corelib.exception.ReceiptXSLTNotFoundException;
import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

/**
 * Created by Ferenc on 2017. 03. 11..
 */
public class ReceiptFormatterEpsonTMT20II implements ReceiptFormatter {
    private static FopFactory fopFactory;

    static private class FormatterResourceResolver implements ResourceResolver{

        @Override
        public Resource getResource(URI uri) throws IOException {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            String path = uri.getPath().substring(1);
            InputStream is = classloader.getResourceAsStream(path);
            return new Resource(uri.getScheme(),is);
        }

        @Override
        public OutputStream getOutputStream(URI uri) throws IOException {
            return null;
        }
    }

    static {
        DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
        Configuration cfg;
        URI base= null;
        try {
            base  = new URI("file:/");
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            cfg = cfgBuilder.build(classloader.getResourceAsStream(Resources.CONFIG.getString("FopConfigFile")));
        } catch (SAXException e) {
            throw new FOPCfgXMLFormatException(e);
        } catch (IOException e) {
            throw new FOPCfgXMLNotFoundException(e);
        } catch (ConfigurationException e) {
            throw new FOPConfigurationErrorException(e);
        } catch (URISyntaxException e) {
            throw new FOPConfigurationErrorException(e);
        }
        ResourceResolverFactory.SchemeAwareResourceResolverBuilder builder =
                ResourceResolverFactory.createSchemeAwareResourceResolverBuilder(ResourceResolverFactory.createDefaultResourceResolver());
        builder.registerResourceResolverForScheme("file",new FormatterResourceResolver());

        FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(base,builder.build()).setConfiguration(cfg);
        fopFactory = fopFactoryBuilder.build();
    }

    private String xslTemplate;

    ReceiptFormatterEpsonTMT20II() {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            StringWriter writer = new StringWriter();
            IOUtils.copy(classloader.getResourceAsStream(Resources.CONFIG.getString("ReceiptXSLTPath")), writer, "UTF-8");
            xslTemplate = writer.toString();
        } catch (IOException e) {
            throw new ReceiptXSLTNotFoundException(Resources.CONFIG.getString("ReceiptXSLTPath"));
        }
    }

    /**
     * Method that will convert the given XML to PDF
     */
    @Override
    public ByteArrayOutputStream convertToPDF(InputStream xml) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
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
            return out;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
