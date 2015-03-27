package com.excelsecu.androidx2j;

import java.util.HashMap;

import org.dom4j.Namespace;

public class Config {
    public static final String PROJECT_PATH = "project/";
    public static final String PROJECT_RES_PATH = PROJECT_PATH + "res/";
    public static final String PROJECT_OUT_ROOT = "project_out/";
    public static final String PACKAGE_NAME = "com.excelsecu.ax2j.test";
    public static final String JAVA_OUT_PATH = PROJECT_OUT_ROOT + "src/" + PACKAGE_NAME.replace('.', '/') + "/";
    public static final String ASSETS_OUT_PATH = PROJECT_OUT_ROOT + "assets/";
    public static final Class<?>[] CLASSES_LIST = com.excelsecu.androidx2j.dbbuilder.Config.CLASSES_LIST;
    public static final String ENCODE = com.excelsecu.androidx2j.dbbuilder.Config.ENCODE;

    public static final String RESOURCES_CLASS = "JResources";
    public static final String TEMPLAT_RESOURCES_CLASS = "Resources";
    public static final String R_CLASS = "JR";
    public static final String RESOURCES_NAME = "resources";
    public static final String UTILS_CLASS = "Utils";
    public static final String TEMPLAT_UTILS_CLASS = "Utils";
    public static final String MAP_OBJECT_NAME = com.excelsecu.androidx2j.dbbuilder.Config.MAP_OBJECT_NAME;
    public static final int API_LEVEL = 8;

    public static final String DEFAULT_THEME = "@android:Theme";

    /**base of R resources id**/
    public static final int BASE = 0x7f040000;

    /**RelativeLayout.Rule's map of attribute to Java value**/
    public static final HashMap<String, String> RULE_MAP = new HashMap<String, String>() {
        private static final long serialVersionUID = 2080190935307088596L;

        {
            put("android:layout_above", "ABOVE");
            put("android:layout_alignBaseline", "ALIGN_BASELINE");
            put("android:layout_alignBottom", "ALIGN_BOTTOM");
            put("android:layout_alignEnd", "ALIGN_END");
            put("android:layout_alignLeft", "ALIGN_LEFT");
            put("android:layout_alignParentBottom", "ALIGN_PARENT_BOTTOM");
            put("android:layout_alignParentEnd", "ALIGN_PARENT_END");
            put("android:layout_alignParentLeft", "ALIGN_PARENT_LEFT");
            put("android:layout_alignParentRight", "ALIGN_PARENT_RIGHT");
            put("android:layout_alignParentStart", "ALIGN_PARENT_START");
            put("android:layout_alignParentTop", "ALIGN_PARENT_TOP");
            put("android:layout_alignRight", "ALIGN_RIGHT");
            put("android:layout_alignStart", "ALIGN_START");
            put("android:layout_alignTop", "ALIGN_TOP");
            //put("android:layout_alignWithParentIfMissing", "");  //no direct rule support
            put("android:layout_below", "BELOW");
            put("android:layout_centerHorizontal", "CENTER_HORIZONTAL");
            //put("android:layout_centerInParent", "");    //no direct rule support
            put("android:layout_centerVertical", "CENTER_VERTICAL");
            put("android:layout_toEndOf", "END_OF");
            put("android:layout_toLeftOf", "LEFT_OF");
            put("android:layout_toRightOf", "RIGHT_OF");
            put("layout_toStartOf", "START_OF");
        }
    };

    /**TextView.InputType's map of attribute to Java value**/
    public static final HashMap<String, String> INPUT_TYPE_MAP = new HashMap<String, String>() {
        private static final long serialVersionUID = 8702820407293253537L;

        {
            put("none", "TYPE_NULL");
            put("text", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_NORMAL");
            put("textCapCharacters", "TYPE_TEXT_FLAG_CAP_CHARACTERS.");
            put("textCapWords", "TYPE_TEXT_FLAG_CAP_WORDS");
            put("textCapSentences", "TYPE_TEXT_FLAG_CAP_SENTENCES.");
            put("textAutoCorrect", "TYPE_TEXT_FLAG_AUTO_CORRECT");
            put("textAutoComplete", "TYPE_TEXT_FLAG_AUTO_COMPLETE");
            put("textMultiLine", " TYPE_TEXT_FLAG_MULTI_LINE");
            put("textImeMultiLine", " TYPE_TEXT_FLAG_IME_MULTI_LINE");
            put("textNoSuggestions", "TYPE_TEXT_FLAG_NO_SUGGESTIONS");
            put("textUri", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_URI");
            put("textEmailAddress", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_EMAIL_ADDRESS");
            put("textEmailSubject", "TYPE_TEXT_VARIATION_EMAIL_SUBJECT");
            put("textShortMessage", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_SHORT_MESSAGE");
            put("textLongMessage", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_LONG_MESSAGE");
            put("textPersonName", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PERSON_NAME");
            put("textPostalAddress", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_POSTAL_ADDRESS");
            put("textPassword", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PASSWORD");
            put("textVisiblePassword", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_VISIBLE_PASSWORD");
            put("textWebEditText", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_EDIT_TEXT");
            put("textFilter", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_FILTER");
            put("textPhonetic", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_PHONETIC");
            put("textWebEmailAddress", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS.");
            put("textWebPassword", "TYPE_CLASS_TEXT | TYPE_TEXT_VARIATION_WEB_PASSWORD");
            put("number", "TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_NORMAL");
            put("numberSigned", "TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED.");
            put("numberDecimal", "TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_DECIMAL");
            put("numberPassword", "TYPE_CLASS_NUMBER | TYPE_NUMBER_VARIATION_PASSWORD");
            put("phone", "TYPE_CLASS_PHONE");
            put("datetime", " TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_NORMAL");
            put("date", "TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_DATE");
            put("time", "TYPE_CLASS_DATETIME | TYPE_DATETIME_VARIATION_TIME");
        }
    };

    public static final String TEMPLET_PACKAGE_NAME = "<!PACKAGE_NAME>";
    public static final String TEMPLET_LAYOUT_BLOCK = "<!LAYOUT_BLOCK>";
    public static final String TEMPLET_NODPI_BLOCK = "<!NODPI_BLOCK>";
    public static final String TEMPLET_LDPI_BLOCK = "<!LDPI_BLOCK>";
    public static final String TEMPLET_MDPI_BLOCK = "<!MDPI_BLOCK>";
    public static final String TEMPLET_HDPI_BLOCK = "<!HDPI_BLOCK>";
    public static final String TEMPLET_XHDPI_BLOCK = "<!XHDPI_BLOCK>";
    public static final String TEMPLET_XXHDPI_BLOCK = "<!XXHDPI_BLOCK>";
    public static final String[] TEMPLET_DPI_BLOCK_LIST = {TEMPLET_NODPI_BLOCK, TEMPLET_LDPI_BLOCK, TEMPLET_MDPI_BLOCK,
        TEMPLET_HDPI_BLOCK, TEMPLET_XHDPI_BLOCK, TEMPLET_XXHDPI_BLOCK};
    public static final String[] DPI_DPI_FOLDER_LIST = {"drawable", "drawable-ldpi", "drawable-mdpi",
        "drawable-hdpi", "drawable-xhdpi", "drawable-xxhdpi"};

    public static final Namespace ANDROID_NAMESPACE = com.excelsecu.androidx2j.dbbuilder.Config.ANDROID_NAMESPACE;
}