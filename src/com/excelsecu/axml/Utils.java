package com.excelsecu.axml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.dom4j.Attribute;

import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.excelsecu.axml.Config;
import com.excelsecu.axml.test.AXMLResources;

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
        if (className.indexOf('.') != -1) {
        	className = className.substring(className.lastIndexOf('.') + 1);
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
        
        return Void.class;
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
        subPath = subPath.substring(0, subPath.indexOf('.')) + ".java";
        String path = Config.JAVA_OUT_PATH + subPath;
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
        System.out.println("Generating " + new File(path).getPath() + "...");
        
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
    public static String buildJavaFile(File file, String content, List<String> importList, List<String> idList) {
        String subPath = file.getPath();
        subPath = subPath.substring(0, subPath.lastIndexOf(File.separator));
        subPath = subPath.substring(subPath.lastIndexOf(File.separator) + 1);
        String title = "package " + Config.PACKAGE_NAME + "." + subPath + ";\n\n";
        
        //add import list
        if (subPath.equals("values")) {
	        if (importList == null) {
	        	importList = new ArrayList<String>();
	        }
        	if (!Utils.hasString(importList, com.excelsecu.axml.test.R.class.getName())) {
        		importList.add(com.excelsecu.axml.test.R.class.getName());
        	}
        	if (!Utils.hasString(importList, SparseArray.class.getName())) {
        		importList.add(SparseArray.class.getName());
        	}
        }
        for (String s : importList) {
            title += "import " + s + ";\n";
        }
        title += "\n";
        
        String className = Utils.getClassName(file);
        title += "public final class " + className + " {\n";
        
        //in this condition, Java file need a return type
        if (!subPath.equals("values")) {
            //find the main object to return
            String returnObject = "";
            String returnClass = "";
            Scanner scan = new Scanner(content);
            while (scan.hasNext()) {
                String str = scan.nextLine();
                //find out the element first built
                if (str.matches("\\w+ \\w+ *= *.+")) {
                    int index = str.indexOf(' ');
                    int index2 = str.indexOf('=', index + 1);
                    returnClass = str.substring(0, index);
                    if (returnClass.equals(AXMLResources.class.getSimpleName()) ||
                            returnClass.equals(InputStream.class.getSimpleName())) {
                        continue;
                    }
                    returnObject = str.substring(index + 1, index2).trim();
                    break;
                }
            }
            scan.close();
            if (returnObject.equals("")) {
                throw new AXMLException(AXMLException.FILE_BUILD_ERROR, "can not find main object");
            }
            
            title += "\n\tpublic static final " + returnClass + " get(Context context) {\n";
            content = "\t\t" + content.replace("\n", "\n\t\t") +
                    "return " + returnObject + ";\n";
            return title + content + "\t}\n}";
        } else {
        	//build a map
        	String fileName = file.getPath();
        	String type = "";
        	String rClass = "";
        	fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.indexOf('.'));
        	switch(fileName) {
        	case "strings":
        		type = String.class.getSimpleName();
        		rClass = "string";
        		break;
        	case "colors":
        		type = Integer.class.getSimpleName();
        		rClass = "color";
        		break;
        	default:
        		type = String.class.getSimpleName();
        		rClass = "string";
        		break;
        	}
        	String map = "\npublic static final SparseArray<" + type + "> map = new SparseArray<" + type + ">() {\n\t{\n";
        	for (String id : idList) {
        		map += "\t\tput(R." + rClass + "." + id + ", " + id + ");\n";
        	}
        	map += "\t}\n};\n";
        	content = content + map;
            content = "\t" + content.replace("\n", "\n\t");
            content = content.substring(0, content.lastIndexOf('\t'));
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
    
    /**
     * Find out whether the attribute has corresponding RelativeLayout rule.
     * @param attr the attribute to find
     * @return the rule of attribute, or null if not found
     */
    public static String findRule(String attrName) {
        return Config.RULE_MAP.get(attrName);
    }
    
    /**
     * Get the name of parent to build the LayoutParams
     * @param node
     * @return
     */
    public static String getParentName(AXMLNode node) {
        if (node.getParent() == null) {
            List<Attribute> attrList = node.getAttributes();
            for (Attribute a : attrList) {
                if (Config.RULE_MAP.get(a.getQualifiedName()) != null) {
                    return RelativeLayout.class.getSimpleName();
                }
                if (a.getQualifiedName().equals("android:layout_gravity")) {
                    return LinearLayout.class.getSimpleName();
                }
            }
            return ViewGroup.class.getSimpleName();
        }
        return node.getParent().getLabelName();
    }
    
    /**
     * Prefix each parameter devided by '|'
     * @param value value to be devided
     * @param prefix prefix of the value
     * @return value added prefix
     */
    public static String prefixParams(String value, String prefix) {
        List<String> list = new ArrayList<String>();
        value = value.toUpperCase();
        while (true) {
            int i = value.indexOf('|');
            if (i == -1) {
                String gravity = value.trim();
                list.add(gravity);
                break;
            }
            String gravity = value.substring(0, i);
            gravity = gravity.trim();
            list.add(gravity);
            value = value.substring(i + 1);
        }
        value = "";
        for (String v : list) {
            value += prefix + "." + v;
            value += " | ";
        }
        value = value.substring(0, value.length() - 3);
        return value;
    }
    
    /**
     * Copy file from oldPath to newPath
     * @param oldPath old file path
     * @param newPath new file path
     * @return true if succeed, false if failed
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File newFile = new File(newPath);
            File newFilePath = new File(newPath.substring(0, newPath.lastIndexOf(File.separatorChar)));
            File oldFile = new File(oldPath);
            if (!newFilePath.isDirectory()) {
                newFilePath.mkdirs();
            }
            if (!newFile.exists()) {
                if (newFile.createNewFile()) {
                    return false;
                }
            }
            
            if (oldFile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream outStream = new FileOutputStream(newPath);
                byte[] buffer = new byte[5120];
                while ((byteread = inStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, byteread);
                }
                inStream.close();
                outStream.close();
                return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get the class name according to the file path
     * @param file the XML file to be converted
     * @return the class name after converted
     */
    public static String getClassName(File file) {
        if (file == null) {
            return "";
        } else if (file.getName().indexOf('.') == -1) {
            return "";
        }
        
        return file.getName().substring(0, file.getName().indexOf('.'));
    }
}