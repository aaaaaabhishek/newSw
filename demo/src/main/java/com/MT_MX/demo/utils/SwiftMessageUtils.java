package com.MT_MX.demo.utils;

import com.MT_MX.demo.Exception.MxParseException;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.iso20022.head_001_001_02.BusinessApplicationHeaderV02;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class SwiftMessageUtils {
private SwiftMessageUtils(){}
    public static String detectMtType(SwiftAst ast) {
        if (ast.getBlock(2) == null) {
            throw new RuntimeException("Missing Block 2 (Application Header)");
        }
        String content = ast.getBlock(2).getContent();
        System.out.println(content);
        if (!content.startsWith("I") || content.length() < 4) {
            throw new RuntimeException("Invalid Block 2 format");
        }
         String mttype=content.substring(1, 4);
        System.out.println("ksks"+mttype);
        if("202".equals(mttype)){
           String priorityCode=ast.getBlock(3).getFieldValue("119");
           System.out.println("priorityCode"+priorityCode);
           if("COV".equals(priorityCode)){
               mttype="202COV";
           }
        }
        return mttype;
    }
    public static String detectMxType(String xml) {

        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("XML input is null or empty");
        }

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
                    new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))
            );

            NodeList docs = doc.getElementsByTagNameNS("*", "Document");

            if (docs.getLength() == 0) {
                throw new MxParseException("MX_001", "ISO 20022 Document element not found");
            }

            Element document = (Element) docs.item(0);

            String namespace = document.getNamespaceURI();

            if (namespace == null || namespace.isBlank()) {
                throw new MxParseException("MX_002","Namespace missing in Document element");
            }

            int lastColon = namespace.lastIndexOf(':');
            if (lastColon == -1 || lastColon == namespace.length() - 1) {
                throw new MxParseException("MX_003","Invalid namespace format: " + namespace);
            }

            String messageDef = namespace.substring(lastColon + 1); // pacs.008.001.13

            String[] parts = messageDef.split("\\.");

            if (parts.length < 2) {
                throw new MxParseException("MX_004","Invalid ISO 20022 message definition: " + messageDef);
            }

            String domain = parts[0]; // pacs
            String type = parts[1];   // 008

            if (!isValidDomain(domain)) {
                throw new MxParseException("MX_003","Invalid domain: " + domain);
            }

            if (!isNumeric(type)) {
                throw new MxParseException("MX_004","Invalid message type: " + type);
            }

            return domain + "." + type;

        } catch (MxParseException e) {
            throw e;
        } catch (Exception e) {
            throw new MxParseException("MX_005","Failed to detect MX message type"+e);
        }
    }

    private static boolean isValidDomain(String domain) {
        return "pacs".equals(domain)
                || "pain".equals(domain)
                || "camt".equals(domain);
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    public static boolean isCover(
            BusinessApplicationHeaderV02 header,
            com.MT_MX.demo.iso20022.pacs_009_001_12.Document doc
    ) {

        if (header != null &&
                header.getBizSvc() != null &&
                header.getBizSvc().toLowerCase().contains("cov")) {

            return true;
        }
        if (doc != null &&
                doc.getFICdtTrf() != null &&
                doc.getFICdtTrf().getCdtTrfTxInf() != null) {

            return doc.getFICdtTrf()
                    .getCdtTrfTxInf()
                    .stream()
                    .anyMatch(tx -> tx.getUndrlygCstmrCdtTrf() != null);
        }

        return false;
    }
    }