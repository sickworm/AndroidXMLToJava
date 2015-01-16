package com.excelsecu.axml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import com.excelsecu.axml.Config;

/**
 * Utils of com.excelsecu.axml package
 * @author ch
 *
 */
public class Utils {
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
    public static void generateFile(File f, String content) {
        String subPath = f.getPath();
        subPath = subPath.substring(4);
        //subPath = subPath.replace(".xml", ".java") is not safety
        subPath = subPath.substring(0, subPath.lastIndexOf('.')) + ".java";
        String path = Config.PROJECT_OUT_PATH + subPath;
        path = path.replace('\\', '/');
        int index = path.lastIndexOf('/');
        if (index == -1) {
            throw new AXMLException(AXMLException.FILE_BUILD_ERROR, path);
        }
        String dir = path.substring(0, index);
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            dirFile.mkdirs();
        }
        
        File javaFile = new File(path);
        System.out.println("Generating " + path + "...");
        
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(javaFile));
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
    
    /**
     * Delete dir with files.
     * @param file directory to be deleted
     */
    public static void deleteDir(File file) {
        if(file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] fileList = file.listFiles();
            for(File f : fileList) {
                deleteDir(f);
                f.delete();
            }
        }
    }
    
    /**
     * Gives the Java code{@param content} a complete construct.
     * @param file the origin XML file
     * @param content Java code translated from XML file
     */
    public static String buildJavaFile(File file, String content, List<String> importList) {
        String subPath = file.getPath();
        subPath = subPath.substring(0, subPath.lastIndexOf(File.separator));
        subPath = subPath.substring(subPath.lastIndexOf(File.separator) + 1);
        //Java class title
        String title = "package " + Config.PACKAGE_NAME + "." + subPath + ";\n\n";
        String className = file.getName();
        //add import list
        if (importList != null) {
            for (String s : importList) {
                title += "import " + s + ";\n";
            }
            title += "\n";
        }
        className = className.substring(0, className.lastIndexOf('.'));
        title += "public class " + className + " {\n";
        //in this condition, Java file need a return type
        if (!subPath.equals("values")) {
            //find the main object to return
            String returnObject = "";
            String returnClass = "";
            Scanner scan = new Scanner(content);
            while (scan.hasNext()) {
                String str = scan.nextLine();
                if (str.matches("\\w+ \\w+ = new \\w+\\(context\\);")) {
                    int index = str.indexOf(' ');
                    int index2 = str.indexOf(' ', index + 1);
                    returnClass = str.substring(0, index);
                    returnObject = str.substring(index + 1, index2);
                    break;
                }
            }
            scan.close();
            if (returnObject.equals("")) {
                throw new AXMLException(AXMLException.FILE_BUILD_ERROR, "can not find main object");
            }
            title += "\tpublic static " + returnClass + " get(Context context) {\n";
            content = "\t\t" + content.replace("\n", "\n\t\t") +
                    "return " + returnObject + ";\n";
            return title + content + "\t}\n}";
        //in this condition, member in this Java file are all 'public static final'
        } else {
            content = "\t" + content.replace("\n", "\n\t");
            return title + content + "}";
        }
    }
    
    /**
     * Find out whether the string is in the list
     * @param list  the list to be searched
     * @param string the string to be found
     * @return
     */
    public static boolean hasString(List<String> list, String string) {
        for (String s : list) {
            if (s.equals(string)) {
                return true;
            }
        }
        return false;
    }
}