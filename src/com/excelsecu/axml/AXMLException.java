package com.excelsecu.axml;

public class AXMLException extends RuntimeException {
    private static final long serialVersionUID = 4451465808378003847L;
    /**succeed**/
    public static final int NO_ERROR = 0x00000000;
    /**can not find the xml file**/
    public static final int AXML_PARSE_ERROR = 0x00000001;
    /**can not find the method relative to the attribute**/
    public static final int METHOD_NOT_FOUND = 0x00000002;
    /**can not find the class relative to the XML label**/
    public static final int CLASS_NOT_FOUND = 0x00000003;
    /**can not find the attribute in the XML node**/
    public static final int ATTRIBUTE_NOT_FOUND = 0x00000004;
    /**can not find the attribute in the XML node**/
    public static final int PROJECT_DIR_NOT_FOUND = 0x00000005;
    /**the parameter haven't initialize**/
    public static final int PARAMETER_NOT_INITIALIZE = 0x00010001;
    /**error when generating the Java file**/
    public static final int FILE_BUILD_ERROR = 0x00020001;
    /**the value of attribute is not correct**/
    public static final int ATTRIBUTE_VALUE_ERROR = 0x00020002;

    private int errorCode = NO_ERROR;
    private String details = "";

    public AXMLException(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public AXMLException(int errorCode, String details) {
        this.errorCode = errorCode;
        this.details = details;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getDetails() {
        return details;
    }
}