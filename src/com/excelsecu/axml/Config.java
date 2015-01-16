package com.excelsecu.axml;

public class Config {
    public static final String PROJECT_RES_PATH = "res/";
    public static final String PROJECT_OUT_ROOT = "res_out/";
    public static final String PACKAGE_NAME = "com.execlsecu.axml";
    public static final String PROJECT_OUT_PATH;
    public static final Class<?>[] CLASSES_LIST = com.excelsecu.axml.dbbuilder.Config.CLASSES_LIST;
    public static final String ENCODE = com.excelsecu.axml.dbbuilder.Config.ENCODE;

    public static final int DIMEN_BASE = 0x7f040000;
    public static final int DRAWABLE_BASE = 0x7f020000;
    public static final int ID_BASE = 0x7f070000;
    public static final int LAYOUT_BASE = 0x7f030000;
    public static final int STRING_BASE = 0x7f050000;
    public static final int STYLE_BASE = 0x7f060000;
    
    static {
        PROJECT_OUT_PATH = PROJECT_OUT_ROOT + PACKAGE_NAME.replace('.', '/') + "/";
    }
}