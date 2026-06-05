package com.MT_MX.demo.service;

import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.mt.MtMapper;
import com.MT_MX.demo.mx.MxDocument;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class MtToMxDocumentMapperFactory {
    private final Map<String, MtMapper> registry;

    public MtToMxDocumentMapperFactory(Map<String, MtMapper> registry) {
        this.registry = registry;
    }

    public MxDocument map(String mtType, SwiftAst ast) {

        MtMapper mapper = registry.get(mtType);

        if (mapper == null) {
            throw new IllegalArgumentException("Unsupported MT: " + mtType);
        }

        return mapper.map(ast);
    }
}
