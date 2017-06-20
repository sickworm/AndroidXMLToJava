package com.sickworm.androidx2j;

public class AX2JException extends RuntimeException {
    private static final long serialVersionUID = 4451465808378003847L;
    /**succeed**/
    public static final int NO_ERROR = 0x00000000;
    /**not a XML file**/
    public static final int AXML_PARSE_ERROR = 0x00000001;
    /**can not find the method relative to the attribute**/
    public static final int METHOD_NOT_FOUND = 0x00000002;
    /**can not find the class relative to the XML label**/
    public static final int CLASS_NOT_FOUND = 0x00000003;
    /**can not find the attribute in the XML node**/
    public static final int ATTRIBUTE_NOT_FOUND = 0x00000004;
    /**can not find the attribute in the XML node**/
    public static final int PROJECT_DIR_NOT_FOUND = 0x00000005;
    /**can not find the attribute in the XML node**/
    public static final int THEME_NOT_FOUND = 0x00000006;
    /**can not find the attribute in the XML node**/
    public static final int STYLE_NOT_FOUND = 0x00000007;
    /**array out of range**/
    public static final int ARRAY_OUT_OF_RANGE = 0x00000008;

    /**the parameter haven't initialize**/
    public static final int PARAMETER_NOT_INITIALIZED = 0x00010001;

    /**error when generating the Java file**/
    public static final int FILE_BUILD_ERROR = 0x00020001;
    /**the value of attribute is not correct**/
    public static final int ATTRIBUTE_VALUE_ERROR = 0x00020002;
    /**system style block in data.dat error**/

    public static final int DAT_SYSTEM_STYLE_ERROR = 0x00030001;
    /**system theme block in data.dat error**/
    public static final int DAT_SYSTEM_THEME_ERROR = 0x00030002;

    private int errorCode = NO_ERROR;

    public AX2JException(int errorCode) {
        super(getDetails(errorCode));
        this.errorCode = errorCode;
    }

    public AX2JException(int errorCode, String details) {
        super(getDetails(errorCode) + "\n" + details);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
    
    public static String getDetails(int errorCode) {
        switch(errorCode) {case NO_ERROR: return "succeed";
        case AXML_PARSE_ERROR: return "XML format invalid";
        case METHOD_NOT_FOUND: return "no such method";
        case CLASS_NOT_FOUND: return "no such class";
        case ATTRIBUTE_NOT_FOUND: return "no such attribute";
        case PROJECT_DIR_NOT_FOUND: return "project not found";
        case THEME_NOT_FOUND: return "theme not found";
        case STYLE_NOT_FOUND: return "style not found";
        case ARRAY_OUT_OF_RANGE: return "array out of range";
        case PARAMETER_NOT_INITIALIZED: return "parameter not initialized";
        case FILE_BUILD_ERROR: return "file build error";
        case ATTRIBUTE_VALUE_ERROR: return "attribute value error";
        case DAT_SYSTEM_STYLE_ERROR: return "style data file format invalid";
        case DAT_SYSTEM_THEME_ERROR: return "theme data file format invalid";
        default: return "unknown error";
        }
    }
}