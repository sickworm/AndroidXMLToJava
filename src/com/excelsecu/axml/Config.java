package com.excelsecu.axml;

import java.util.HashMap;

public class Config {
    public static final String PROJECT_RES_PATH = "res/";
    public static final String PROJECT_OUT_ROOT = "res_out/";
    public static final String PACKAGE_NAME = "com.excelsecu.axml.test";
    public static final String PROJECT_OUT_PATH;
    public static final Class<?>[] CLASSES_LIST = com.excelsecu.axml.dbbuilder.Config.CLASSES_LIST;
    public static final String ENCODE = com.excelsecu.axml.dbbuilder.Config.ENCODE;

    public static final int DIMEN_BASE = 0x7f040000;
    public static final int DRAWABLE_BASE = 0x7f020000;
    public static final int ID_BASE = 0x7f070000;
    public static final int LAYOUT_BASE = 0x7f030000;
    public static final int STRING_BASE = 0x7f050000;
    public static final int STYLE_BASE = 0x7f060000;
    
    public static final HashMap<String, String> RULE_MAP = new HashMap<String, String>();;
    
    static {
        PROJECT_OUT_PATH = PROJECT_OUT_ROOT + PACKAGE_NAME.replace('.', '/') + "/";
        
        RULE_MAP.put("android:layout_above", "ABOVE");
        RULE_MAP.put("android:layout_alignBaseline", "ALIGN_BASELINE");
        RULE_MAP.put("android:layout_alignBottom", "ALIGN_BOTTOM");
        RULE_MAP.put("android:layout_alignEnd", "ALIGN_END");
        RULE_MAP.put("android:layout_alignLeft", "ALIGN_LEFT");
        RULE_MAP.put("android:layout_alignParentBottom", "ALIGN_PARENT_BOTTOM");
        RULE_MAP.put("android:layout_alignParentEnd", "ALIGN_PARENT_END");
        RULE_MAP.put("android:layout_alignParentLeft", "ALIGN_PARENT_LEFT");
        RULE_MAP.put("android:layout_alignParentRight", "ALIGN_PARENT_RIGHT");
        RULE_MAP.put("android:layout_alignParentStart", "ALIGN_PARENT_START");
        RULE_MAP.put("android:layout_alignParentTop", "ALIGN_PARENT_TOP");
        RULE_MAP.put("android:layout_alignRight", "ALIGN_RIGHT");
        RULE_MAP.put("android:layout_alignStart", "ALIGN_START");
        RULE_MAP.put("android:layout_alignTop", "ALIGN_TOP");
        //RULE_MAP.put("android:layout_alignWithParentIfMissing", "");  //no direct rule support
        RULE_MAP.put("android:layout_below", "BELOW");
        RULE_MAP.put("android:layout_centerHorizontal", "CENTER_HORIZONTAL");
        //RULE_MAP.put("android:layout_centerInParent", "");    //no direct rule support
        RULE_MAP.put("android:layout_centerVertical", "CENTER_VERTICAL");
        RULE_MAP.put("android:layout_toEndOf", "END_OF");
        RULE_MAP.put("android:layout_toLeftOf", "LEFT_OF");
        RULE_MAP.put("android:layout_toRightOf", "RIGHT_OF");
        RULE_MAP.put("layout_toStartOf", "START_OF");
    }
}