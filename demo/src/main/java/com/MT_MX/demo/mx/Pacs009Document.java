package com.MT_MX.demo.mx;
import com.MT_MX.demo.iso20022.pacs_009_001_12.Document;
public class Pacs009Document implements MxDocument{
    private Document doc202;
    public Document getDoc202() {
        return doc202;
    }
    public void setDoc202(Document doc202) {
        this.doc202 = doc202;
    }
}
