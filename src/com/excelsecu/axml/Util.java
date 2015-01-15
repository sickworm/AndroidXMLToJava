package com.excelsecu.axml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.excelsecu.axml.Config;

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
    
    /**
     * Find out the class which matches the XML label
     * @param XML label
     * @return the class matches the XML label or null when not found
     */
    public static Class<?> matchClass(String name) {
        for (int i = 0; i < Config.CLASSES_LIST.length; i++) {
            String className = "";
            if (name.contains("support")) {
                className = Config.CLASSES_LIST[i].getName();
            } else {
                className = Config.CLASSES_LIST[i].getSimpleName();
            }
            if (className.equals(name)) {
                return Config.CLASSES_LIST[i];
            }
        }
        return null;
    }
    
    /**
     * 
     * Generate Java file according to the path and content. Auto override and create directory.
     * @param path the path of the file to be built
     * @param content the content of the file
     */
    public static void generateFile(File f, String content, boolean append) {
        String subPath = f.getPath();
        subPath = subPath.substring(4);
        //subPath = subPath.replace(".xml", ".java") is not safety
        subPath = subPath.substring(0, subPath.lastIndexOf('.')) + ".java";
        String path = Config.PROJECT_OUT_PATH + subPath;
        int index = path.lastIndexOf(File.separator);
        if (index == -1) {
            throw new AXMLException(AXMLException.FILE_BUILD_ERROR, path);
        }
        String dir = path.substring(0, index);
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dirFile.mkdirs();
        }
        
        File javaFile = new File(path);
        if (append && javaFile.exists() && javaFile.isFile()) {
            System.out.println("Append content to" + path + "...");
        } else {
            System.out.println("Generating " + path + "...");
        }
        
        if (!javaFile.exists()) {
            try {
                javaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new AXMLException(AXMLException.FILE_BUILD_ERROR, path);
            }
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(javaFile, append));
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AXMLException(AXMLException.FILE_BUILD_ERROR, path);
        }
    }
    
    /**
     * Get the file extension, return "" if no extension
     * @param file
     * @return extension of the file
     */
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}