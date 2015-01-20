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

    public static final HashMap<String, String> RULE_MAP = new HashMap<String, String>();
    public static final HashMap<String, String> INPUT_TYPE_MAP = new HashMap<String, String>();
    
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

        INPUT_TYPE_MAP.put("none", "TYPE_NULL");
        INPUT_TYPE_MAP.put("text", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL");
        INPUT_TYPE_MAP.put("textCapCharacters", "TYPE_TEXT_FLAG_CAP_CHARACTERS.");
        INPUT_TYPE_MAP.put("textCapWords", "TYPE_TEXT_FLAG_CAP_WORDS");
        INPUT_TYPE_MAP.put("textCapSentences", "TYPE_TEXT_FLAG_CAP_SENTENCES.");
        INPUT_TYPE_MAP.put("textAutoCorrect", "TYPE_TEXT_FLAG_AUTO_CORRECT");
        INPUT_TYPE_MAP.put("textAutoComplete", "TYPE_TEXT_FLAG_AUTO_COMPLETE");
        INPUT_TYPE_MAP.put("textMultiLine", " TYPE_TEXT_FLAG_MULTI_LINE");
        INPUT_TYPE_MAP.put("textImeMultiLine", " TYPE_TEXT_FLAG_IME_MULTI_LINE");
        INPUT_TYPE_MAP.put("textNoSuggestions", "TYPE_TEXT_FLAG_NO_SUGGESTIONS");
        INPUT_TYPE_MAP.put("textUri", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_URI");
        INPUT_TYPE_MAP.put("textEmailAddress", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS");
        INPUT_TYPE_MAP.put("textEmailSubject", "TYPE_TEXT_VARIATION_EMAIL_SUBJECT");
        INPUT_TYPE_MAP.put("textShortMessage", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_SHORT_MESSAGE");
        INPUT_TYPE_MAP.put("textLongMessage", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_LONG_MESSAGE");
        INPUT_TYPE_MAP.put("textPersonName", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME");
        INPUT_TYPE_MAP.put("textPostalAddress", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_POSTAL_ADDRESS");
        INPUT_TYPE_MAP.put("textPassword", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD");
        INPUT_TYPE_MAP.put("textVisiblePassword", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD");
        INPUT_TYPE_MAP.put("textWebEditText", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_EDIT_TEXT");
        INPUT_TYPE_MAP.put("textFilter", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_FILTER");
        INPUT_TYPE_MAP.put("textPhonetic", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PHONETIC");
        INPUT_TYPE_MAP.put("textWebEmailAddress", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS.");
        INPUT_TYPE_MAP.put("textWebPassword", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_PASSWORD");
        INPUT_TYPE_MAP.put("number", "TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_NORMAL");
        INPUT_TYPE_MAP.put("numberSigned", "TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED.");
        INPUT_TYPE_MAP.put("numberDecimal", "TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL");
        INPUT_TYPE_MAP.put("numberPassword", "TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_PASSWORD");
        INPUT_TYPE_MAP.put("phone", "TYPE_CLASS_PHONE");
        INPUT_TYPE_MAP.put("datetime", " TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_NORMAL");
        INPUT_TYPE_MAP.put("date", "TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_DATE");
        INPUT_TYPE_MAP.put("time", "TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_TIME");
    }
}