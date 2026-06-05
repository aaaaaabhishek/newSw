package com.MT_MX.demo.Exception;
public class MxParseException extends RuntimeException {

    private final String errorCode;

    public MxParseException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}