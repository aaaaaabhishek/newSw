package com.MT_MX.demo.ast;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.StringReader;
import org.xml.sax.InputSource;

public final class SwiftMxAst {

    private final MxElementNode root;

    public SwiftMxAst(MxElementNode root) {
        this.root = root;
    }

    public MxElementNode getRoot() {
        return root;
    }

    // path lookup like: Document/FIToFICstmrCdtTrf/GrpHdr/MsgId
    public String getValue(String path) {

        String[] parts = path.split("/");
        MxElementNode current = root;

        for (String p : parts) {
            if (current == null) return null;
            current = current.getChild(p);
        }

        return current != null ? current.getText() : null;
    }
}