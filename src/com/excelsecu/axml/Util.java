package com.excelsecu.axml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.excelsecu.axml.dbbuilder.Config;

/**
 * Util of com.excelsecu.axml package
 * @author ch
 *
 */
public class Util {
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
        for (Class<?> c : Config.CLASSES_LIST) {
            if (c.equals(type)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Read a file
     * @param fileName
     * @return content of file, return "" if error occurs
     */
    public static String readFile(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), Config.ENCODE));
            String content = "";
            String buf;
            
            while ((buf = reader.readLine())!= null) {
                content += buf + "\n";
            }
            reader.close();
            return content;
        }
        catch( Exception e ) {
            e.printStackTrace();
            return "";
        }
    }
}