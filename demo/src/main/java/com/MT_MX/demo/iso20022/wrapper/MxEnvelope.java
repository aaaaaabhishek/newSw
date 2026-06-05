package com.MT_MX.demo.iso20022.wrapper;

import com.MT_MX.demo.iso20022.head_001_001_02.BusinessApplicationHeaderV02;
import com.MT_MX.demo.iso20022.pacs.Document;
import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "Envelope")
@XmlAccessorType(XmlAccessType.FIELD)
public class  MxEnvelope {

    @XmlElement(name = "-", namespace = "urn:iso:std:iso:20022:tech:xsd:head.001.001.02")
    private BusinessApplicationHeaderV02 appHdr;

    public Object getDocument() {
        return document;
    }

    public void setDocument(Object document) {
        this.document = document;
    }

    //    @XmlElement(name = "Document", namespace = "urn:iso:std:iso:20022:tech:xsd:pacs.008.001.13")
//    private Document document;
//
//    public void setDocument(Document document) {
//        this.document = document;
//    }
@XmlAnyElement(lax = true)
private Object document;
    public void setAppHdr(BusinessApplicationHeaderV02 appHdr) {
        this.appHdr = appHdr;
    }
//    public Document getDocument() {
//        return document;
//    }

    public BusinessApplicationHeaderV02 getAppHdr() {
        return appHdr;
    }
}