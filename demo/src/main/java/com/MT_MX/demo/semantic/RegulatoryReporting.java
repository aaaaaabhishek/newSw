package com.MT_MX.demo.semantic;
import java.util.List;

public class RegulatoryReporting {

    private final String code;
    private final List<String> values;

    public RegulatoryReporting(String code, List<String> values) {
        this.code = code;
        this.values = values;
    }

    public String getCode() {
        return code;
    }

    public List<String> getValues() {
        return values;
    }
}