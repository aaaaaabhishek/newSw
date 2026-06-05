package com.MT_MX.demo.mt;

import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.mx.MxDocument;
import com.MT_MX.demo.mx.Pacs008Document;
import com.MT_MX.demo.utils.DocumentUtil;
import org.springframework.stereotype.Component;

@Component("108")
public class Mt108Mapper implements MtMapper{
  Pacs008Document doc=new Pacs008Document();
    @Override
    public MxDocument map(SwiftAst ast) {
        try {
            doc.setDoc108(DocumentUtil.buildMX(ast));
        } catch (Exception e) {
        }
        return doc;
    }
}