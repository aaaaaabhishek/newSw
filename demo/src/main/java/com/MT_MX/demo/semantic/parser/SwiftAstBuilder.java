package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.StringReader;
import org.xml.sax.InputSource;

public final class SwiftAstBuilder {

    private final SwiftBlockParser blockParser = new SwiftBlockParser();
    private final SwiftFieldParser fieldParser = new SwiftFieldParser();

    public SwiftAst buildAst(String rawMessage) throws Exception {
        SwiftAst ast = new SwiftAst();
   //     SwiftAstC astn = new SwiftAstC();

        SwiftBlocks blocks = blockParser.parse(rawMessage);
        for (int blockNo = 1; blockNo <= 5; blockNo++) {
            String content = blocks.getBlock(blockNo);
            if (content != null) {
                BlockNode blockNode;
                if(blockNo==3) {
                    blockNode = fieldParser.parseBlock3(blockNo,content);
                }else {
                    blockNode = fieldParser.parseBlock(blockNo, content);
                }
                      ast.addBlock(blockNode);
//                if (blocks.getBlock1() != null)
//                    astn.setBlock1(new BasicHeaderBlock(blocks.getBlock1()));
//
//                if (blocks.getBlock2() != null)
//                    astn.setBlock2(new ApplicationHeaderBlock(blocks.getBlock2()));
//
//                if (blocks.getBlock3() != null)
//                    astn.setBlock3(fieldParser.parseBlock3(3, blocks.getBlock3()));
//
//                if (blocks.getBlock4() != null)
//                    astn.setBlock4(fieldParser.parseBlock(4, blocks.getBlock4()));
//
//                if (blocks.getBlock5() != null) {
//                    BlockNode b5 = new BlockNode(5);
//                    b5.setContent(blocks.getBlock5());
//                    astn.setBlock5(b5);
//                }
            }
        }
        return ast;
    }
    public SwiftMxAst build(String xml) {

        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);

            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(new InputSource(new StringReader(xml)));

            Element envelopeRoot = doc.getDocumentElement();

            Element documentEl = findFirstElementByLocalName(envelopeRoot, "Document");

            if (documentEl == null) {
                throw new RuntimeException("MX Document element not found");
            }

            return new SwiftMxAst(convert(documentEl));
        } catch (Exception e) {
            throw new RuntimeException("MX parse failed", e);
        }
    }
    private Element findFirstElementByLocalName(Element e, String name) {

        if (name.equals(e.getLocalName()))
            return e;

        NodeList list = e.getChildNodes();

        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);

            if (n instanceof Element child) {
                Element found = findFirstElementByLocalName(child, name);
                if (found != null) return found;
            }
        }

        return null;
    }
    private MxElementNode convert(Element e) {

        String name =
                e.getLocalName() != null
                        ? e.getLocalName()
                        : e.getTagName();

        MxElementNode node = new MxElementNode(name);
        // attributes
        NamedNodeMap attrs = e.getAttributes();
        for (int i=0;i<attrs.getLength();i++) {
            Attr a = (Attr) attrs.item(i);
            node.addAttribute(a.getName(), a.getValue());
        }

        NodeList list = e.getChildNodes();

        for (int i=0;i<list.getLength();i++) {
            Node n = list.item(i);

            if (n instanceof Element childEl) {
                node.addChild(convert(childEl));
            }
            else if (n.getNodeType() == Node.TEXT_NODE) {
                String t = n.getTextContent().trim();
                if (!t.isEmpty()) node.setText(t);
            }
        }

        return node;
    }

}
