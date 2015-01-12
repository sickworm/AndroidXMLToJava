package com.excelsecu.ian.axml.dbbuilder;

public class AndroidDocException extends RuntimeException{
	private static final long serialVersionUID = -611799962850239497L;
	private int errorCode = NO_ERROR;
    /**succeed**/
    public static final int NO_ERROR = 0x00000000;
    /**Android doc XML format error**/
    public static final int ATM_FORMAT_ERROR = 0x00000001;
    /**Android doc 'attribute to method' table format error**/
    public static final int AXML_FORMAT_ERROR = 0x00000002;
    
    public AndroidDocException(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
}
