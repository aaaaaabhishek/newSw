package com.MT_MX.demo.mt;

import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.mx.MxDocument;

public interface MtMapper {
    MxDocument map(SwiftAst ast);
}
