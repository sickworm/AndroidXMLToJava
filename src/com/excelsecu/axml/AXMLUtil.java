package com.excelsecu.axml;

import com.excelsecu.axml.dbbuilder.AndroidDocConfig;

/**
 * Util of com.excelsecu.axml package
 * @author ch
 *
 */
public class AXMLUtil {
    /**
     * Transform a class name to a class object name. Change the first letter to lower case.
     */
    public static String classToObject(String className) {
        if (className == null || className.length() < 1) {
            return null;
        }
        char firstLetter = className.charAt(0);
        firstLetter = Character.toLowerCase(firstLetter);
        String objectName = className.substring(1);
        objectName = firstLetter + objectName;
        return objectName;
    }
    
    /**
     * Check the class if it's in the support list CLASSES_LIST.
     */
    public static boolean isSupportClass(Class<?> type) {
        for (Class<?> c : AndroidDocConfig.CLASSES_LIST) {
            if (c.equals(type)) {
                return true;
            }
        }
        return false;
    }
}