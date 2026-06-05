package com.MT_MX.demo.mt;

import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.mx.MxDocument;
import com.MT_MX.demo.mx.Pacs009Document;
import com.MT_MX.demo.utils.Document202;
import org.springframework.stereotype.Component;

@Component("202COV")
public class MT202COVMapper implements MtMapper{
    Pacs009Document doc = new Pacs009Document();
    @Override
    public MxDocument map(SwiftAst ast) {
        try {
            doc.setDoc202(Document202.buildMX(ast));
        } catch (Exception e) {
        }
        return doc;
    }
}
