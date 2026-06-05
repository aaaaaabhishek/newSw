package com.MT_MX.demo.utils;
import com.MT_MX.demo.Exception.MxParseException;
import com.MT_MX.demo.entity.ExtractedMx;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class MxExtractor {

    public static ExtractedMx extract(String xml) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Security (XXE protection)
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(
                    new ByteArrayInputStream(xml.getBytes())
            );

            Node appHdrNode = doc.getElementsByTagNameNS("*", "AppHdr").item(0);
            Node documentNode = doc.getElementsByTagNameNS("*", "Document").item(0);

            if (appHdrNode == null || documentNode == null) {
                throw new MxParseException("app_001","AppHdr or Document not found in SAA message");
            }

            String appHdrXml = toString(appHdrNode);
            System.out.println(appHdrXml);
            String documentXml = toString(documentNode);

            return new ExtractedMx(appHdrXml, documentXml);

        } catch (Exception e) {
            throw new MxParseException("app_002","Failed to extract MX parts"+ e);
        }
    }

    private static String toString(Node node) throws Exception {

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        StringWriter writer = new StringWriter();
        /// remove xml namespace
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(node), new StreamResult(writer));

        return writer.toString();
    }
}