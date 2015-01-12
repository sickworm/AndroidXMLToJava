package com.excelsecu.axml;

public class AXMLException extends RuntimeException {
    private int errorCode = NO_ERROR;
    private static final long serialVersionUID = 4451465808378003847L;
    /**succeed**/
    public static final int NO_ERROR = 0x00000000;
    /**can not find the xml file**/
    public static final int FILE_NOT_FOUND = 0x00000001;
    /**can not find the method relative to the attribute**/
    public static final int METHOD_NOT_FOUND = 0x00000001;
    
    public AXMLException(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}