package com.MT_MX.demo.utils;
import com.MT_MX.demo.entity.ExtractedMx;
public class MxNormalizer {

    public static String normalize(String xml) {

        if (xml == null || xml.isBlank()) {
            return xml;
        }

        if (xml.contains("urn:swift:saa:xsd")) {

            ExtractedMx extracted = MxExtractor.extract(xml);

            String appHdr = wrapIfNeeded(extracted.getAppHdrXml(),"");
            String document = wrapIfNeeded(extracted.getDocumentXml(), "");
            StringBuffer buffer = new StringBuffer();
            buffer.append("<Envelope>").append("\n")
                    .append(appHdr).append("\n")
                    .append(document).append("\n")
                    .append("</Envelope>");
            System.out.println(buffer.toString());
            return buffer.toString();
        }

        return xml;
    }

    private static String wrapIfNeeded(String xmlFragment, String tagName) {

        if (xmlFragment == null) return "";

        xmlFragment = xmlFragment.trim();

        // already wrapped
        if (xmlFragment.startsWith("<" + tagName)) {
            return xmlFragment;
        }

        return "<" + tagName + ">" + xmlFragment + "</" + tagName + ">";
    }
}