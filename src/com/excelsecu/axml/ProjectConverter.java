package com.excelsecu.axml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ProjectConverter {
    private static List<String> dimenList = new ArrayList<String>();
    private static List<String> drawableList = new ArrayList<String>();
    private static List<String> idList = new ArrayList<String>();
    private static List<String> layoutList = new ArrayList<String>();
    private static List<String> stringList = new ArrayList<String>();
    private static List<String> styleList = new ArrayList<String>();
    private static final String[] LIST_ORDER = {"dimen", "drawable", "id"
        , "layout", "string", "style"};
    private static final List<List<String>> LIST_ORDER_LIST = new ArrayList<List<String>>();
    static {
        LIST_ORDER_LIST.add(dimenList);
        LIST_ORDER_LIST.add(drawableList);
        LIST_ORDER_LIST.add(idList);
        LIST_ORDER_LIST.add(layoutList);
        LIST_ORDER_LIST.add(stringList);
        LIST_ORDER_LIST.add(styleList);
    }
    private static final int[] LIST_BASE = {Config.DIMEN_BASE, Config.DRAWABLE_BASE,
        Config.ID_BASE, Config.LAYOUT_BASE, Config.STRING_BASE, Config.STYLE_BASE};
    
    public static void main(String[] argv) {
        File res = new File(Config.PROJECT_RES_PATH);
        if (!res.isDirectory()) {
            throw new AXMLException(AXMLException.PROJECT_DIR_NOT_FOUND);
        }
        File resOut = new File(Config.PROJECT_OUT_PATH);
        if (resOut.exists()) {
            Utils.deleteDir(resOut);
        }
        
        File[] dirList = res.listFiles();
        for (File f : dirList) {
            String path = f.getPath();
            if (f.isFile()) {
                continue;
            }
            
            if (path.matches(".+layout")) {
                LayoutOutput(f);
            } else if (path.matches(".+anim")) {
            } else if (path.matches(".+drawable.*")) {
            } else if (path.matches(".+menu.*")) {
            } else if (path.matches(".+values.*")) {
                ValueOutput(f);
            }
        }
        GenerateR();
    }
    
    private static void LayoutOutput(File dir) {
        File[] fileList = dir.listFiles();
        for (File f : fileList) {
            System.out.println("Analysing " + f.getName() + "...");
            if (!Utils.getFileExtension(f).equals("xml")) {
                continue;
            }
            if (f.isFile() && f.getName().endsWith(".xml")) {
                try {
                    LayoutConverter converter = new LayoutConverter(f.getPath());
                    String content = converter.convertAsString();
                    idList.addAll(converter.getIdList());
                    try {
                        content = Utils.buildJavaFile(f, content);
                    } catch (AXMLException e) {
                        System.out.println(f.getName() + " build Java file error: " +
                                e.getErrorCode() + " " + e.getDetails() + "");
                        content = "//Temp file. Error occured when building this file.\n" +
                                "//Error: " + e.getErrorCode() + " " + e.getDetails() + content;
                    }
                    Utils.generateFile(f, content, false);
                } catch (AXMLException e) {
                    System.out.println(f.getName() + " convert error: " +
                            e.getErrorCode() + " " + e.getDetails() + "");
                    e.printStackTrace();
                }
            }
            System.out.println("");
        }
    }
    
    private static void ValueOutput(File valueFile) {
        File[] fileList = valueFile.listFiles();
        for (File valuesF : fileList) {
            System.out.println("Analysing " + valuesF.getName() + "...");
            StringOutput(valuesF);
            System.out.println("");
        }
    }
    
    private static void StringOutput(File stringFile) {
        Document document = null;
        try {
            document = new SAXReader().read(stringFile).getDocument();
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new AXMLException(AXMLException.AXML_PARSE_ERROR);
        }
        Element root = document.getRootElement();
        if (!root.getName().equals("resources"))
            return;
        @SuppressWarnings("unchecked")
        List<Element> list = root.elements();
        String content = "";
        for (Element e : list) {
            if (e.getName().equals("string")) {
                content += "public static final String " + e.attributeValue("name") +
                        " = \"" + e.getText() + "\";\n";
                stringList.add(e.attributeValue("name"));
            }
        }
        
        if (content.equals("")) {
            return;
        }
        String path = stringFile.getPath();
        path = path.substring(0, path.lastIndexOf(File.separator) + 1);
        path += "strings.xml";
        File file = new File(path);
        content = Utils.buildJavaFile(file, content);
        Utils.generateFile(file, content, true);
    }
    
    private static void GenerateR() {
        String content = "";
        content += "package " + Config.PACKAGE_NAME + ";\n\npublic final class R {\n";
        for (int i =0; i < LIST_ORDER.length; i++) {
            List<String> list = LIST_ORDER_LIST.get(i);
            content += "\tpublic static final class " + LIST_ORDER[i] + "{\n";
            for (int j = 0; j < list.size(); j++) {
                content += "\t\tpublic static final int " + list.get(j) +
                        " = 0x" + Integer.toHexString(LIST_BASE[i] + j) + ";\n";
            }
            content += "\t}\n\n";
            content += "}";
        }
        
        String rPath = Config.PROJECT_OUT_PATH + "R.java";
        System.out.println("Generating " + rPath + "...");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(rPath));
            out.write(content);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new AXMLException(AXMLException.FILE_BUILD_ERROR, rPath);
        }
    }
    
}
