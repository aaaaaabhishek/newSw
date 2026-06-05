package com.MT_MX.demo.entity;

public class ExtractedMx {

    private final String appHdrXml;
    private final String documentXml;

    public ExtractedMx(String appHdrXml, String documentXml) {
        this.appHdrXml = appHdrXml;
        this.documentXml = documentXml;
    }

    public String getAppHdrXml() {
        return appHdrXml;
    }

    public String getDocumentXml() {
        return documentXml;
    }
}