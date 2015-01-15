package com.excelsecu.axml;

public class Config {
    public static final String PROJECT_RES_PATH = "res/";
    public static final String PROJECT_OUT_ROOT = "res_out/";
    public static final String PACKAGE_NAME = "com.execlsecu.axml/";
    public static final String PROJECT_OUT_PATH;
    public static final Class<?>[] CLASSES_LIST = com.excelsecu.axml.dbbuilder.Config.CLASSES_LIST;
    public static final String ENCODE = com.excelsecu.axml.dbbuilder.Config.ENCODE;
    
    static {
        PROJECT_OUT_PATH = PROJECT_OUT_ROOT + PACKAGE_NAME.replace('.', '/') + "R/";
    }
}