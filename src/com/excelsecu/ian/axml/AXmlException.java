package com.excelsecu.ian.axml;

public class AXmlException extends RuntimeException {
    private int errorCode = NO_ERROR;
    private static final long serialVersionUID = 4451465808378003847L;
    /**操作成功**/
    public static final int NO_ERROR = 0x00000000;
    /**找不到xml文件**/
    public static final int FILE_NOT_FOUND = 0x00000001;
    
    public AXmlException(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}