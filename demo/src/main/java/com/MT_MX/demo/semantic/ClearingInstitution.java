package com.MT_MX.demo.semantic;

public class ClearingInstitution {
    private String clearingSystem;
    private String clearingCode;

    public ClearingInstitution(String clearingSystem, String clearingCode) {
        this.clearingSystem = clearingSystem;
        this.clearingCode = clearingCode;
    }
    public String getClearingSystem() {
        return clearingSystem;
    }

    public String getClearingCode() {
        return clearingCode;
    }
}
